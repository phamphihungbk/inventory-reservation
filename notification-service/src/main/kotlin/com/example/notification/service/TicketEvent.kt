package com.example.notification.service

import java.math.BigDecimal
import java.time.Instant

data class TicketEvent(
    val eventId: String,
    val eventType: String,
    val occurredAt: Instant,
    val reservationId: String? = null,
    val ticketTypeId: String? = null,
    val quantity: Int? = null,
    val paymentId: String? = null,
    val orderId: String? = null,
    val totalAmount: BigDecimal? = null,
)
