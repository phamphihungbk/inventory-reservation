package com.example.inventory.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

object KafkaTopics {
    const val RESERVATION_CREATED = "reservation.created"
    const val RESERVATION_EXPIRED = "reservation.expired"
    const val RESERVATION_CANCELLED = "reservation.cancelled"
    const val PAYMENT_SUCCEEDED = "payment.succeeded"
    const val PAYMENT_FAILED = "payment.failed"
    const val TICKET_PURCHASED = "ticket.purchased"
    const val INVENTORY_CHANGED = "inventory.changed"
}

@Component
class TicketEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    fun publish(topic: String, event: TicketEvent) {
        kafkaTemplate.send(topic, event.eventId, objectMapper.writeValueAsString(event))
    }
}
