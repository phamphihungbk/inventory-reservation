package com.example.inventory.outbox

import com.example.inventory.kafka.TicketEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant

@Service
class OutboxEventService(
    private val outboxEventRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper,
    private val clock: Clock,
) {
    fun enqueue(topic: String, event: TicketEvent) {
        val now = Instant.now(clock)
        outboxEventRepository.save(
            OutboxEvent(
                topic = topic,
                eventKey = event.eventId,
                payload = objectMapper.writeValueAsString(event),
                nextAttemptAt = now,
                createdAt = now,
            ),
        )
    }
}
