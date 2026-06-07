package com.example.inventory.outbox

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.math.min

@Component
class OutboxEventPublisher(
    private val outboxEventRepository: OutboxEventRepository,
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val clock: Clock,
    @Value("\${outbox.publisher.batch-size:50}")
    private val batchSize: Int,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedDelayString = "\${outbox.publisher.fixed-delay-ms:2000}")
    @Transactional
    fun publishPending() {
        val now = Instant.now(clock)
        val events = outboxEventRepository.findPublishable(
            statuses = listOf(OutboxEventStatus.PENDING, OutboxEventStatus.FAILED),
            now = now,
            pageable = PageRequest.of(0, batchSize),
        )

        events.forEach { event ->
            try {
                kafkaTemplate.send(event.topic, event.eventKey, event.payload).get(5, TimeUnit.SECONDS)
                event.status = OutboxEventStatus.PUBLISHED
                event.publishedAt = Instant.now(clock)
            } catch (ex: Exception) {
                event.attemptCount += 1
                event.status = OutboxEventStatus.FAILED
                event.nextAttemptAt = Instant.now(clock).plusSeconds(backoffSeconds(event.attemptCount))
                log.warn(
                    "Failed to publish outbox event {} topic {} attempt {}",
                    event.id,
                    event.topic,
                    event.attemptCount,
                    ex,
                )
            }
        }
    }

    private fun backoffSeconds(attemptCount: Int): Long =
        min(60L, 1L shl min(attemptCount, 6))
}
