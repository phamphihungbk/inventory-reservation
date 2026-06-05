package com.example.inventory.kafka

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class TicketEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val eventType: String,
    val occurredAt: Instant = Instant.now(),
    val reservationId: String? = null,
    val ticketTypeId: String? = null,
    val quantity: Int? = null,
    val paymentId: String? = null,
    val orderId: String? = null,
    val totalAmount: BigDecimal? = null,
)
