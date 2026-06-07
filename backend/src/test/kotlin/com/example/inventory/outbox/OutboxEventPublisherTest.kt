package com.example.inventory.outbox

import io.mockk.every
import io.mockk.mockk
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Pageable
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.concurrent.CompletableFuture

class OutboxEventPublisherTest {
    private val repository = mockk<OutboxEventRepository>()
    private val kafkaTemplate = mockk<KafkaTemplate<String, String>>()
    private val clock = Clock.fixed(Instant.parse("2026-05-08T10:00:00Z"), ZoneOffset.UTC)
    private val publisher = OutboxEventPublisher(repository, kafkaTemplate, clock, 50)

    @Test
    fun `marks sent outbox event as published`() {
        val event = outboxEvent()
        every { repository.findPublishable(any(), any(), any<Pageable>()) } returns listOf(event)
        every { kafkaTemplate.send("reservation.created", "event-1", "{}") } returns CompletableFuture.completedFuture(
            SendResult(ProducerRecord("reservation.created", "event-1", "{}"), mockk()),
        )

        publisher.publishPending()

        assertEquals(OutboxEventStatus.PUBLISHED, event.status)
        assertEquals(Instant.parse("2026-05-08T10:00:00Z"), event.publishedAt)
    }

    @Test
    fun `keeps failed event retryable with backoff`() {
        val event = outboxEvent()
        every { repository.findPublishable(any(), any(), any<Pageable>()) } returns listOf(event)
        every { kafkaTemplate.send("reservation.created", "event-1", "{}") } throws RuntimeException("kafka down")

        publisher.publishPending()

        assertEquals(OutboxEventStatus.FAILED, event.status)
        assertEquals(1, event.attemptCount)
        assertTrue(event.nextAttemptAt.isAfter(Instant.parse("2026-05-08T10:00:00Z")))
    }

    private fun outboxEvent() = OutboxEvent(
        id = 1,
        topic = "reservation.created",
        eventKey = "event-1",
        payload = "{}",
        nextAttemptAt = Instant.parse("2026-05-08T10:00:00Z"),
        createdAt = Instant.parse("2026-05-08T09:59:00Z"),
    )
}
