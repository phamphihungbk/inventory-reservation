package com.example.inventory.kafka

object KafkaTopics {
    const val RESERVATION_CREATED = "reservation.created"
    const val RESERVATION_EXPIRED = "reservation.expired"
    const val RESERVATION_CANCELLED = "reservation.cancelled"
    const val PAYMENT_SUCCEEDED = "payment.succeeded"
    const val PAYMENT_FAILED = "payment.failed"
    const val TICKET_PURCHASED = "ticket.purchased"
    const val INVENTORY_CHANGED = "inventory.changed"
}
