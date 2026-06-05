package com.example.inventory.mapper

import com.example.inventory.dto.OrderResponse
import com.example.inventory.dto.PaymentResponse
import com.example.inventory.entity.Payment
import com.example.inventory.entity.TicketOrder

fun Payment.toResponse(orderId: Long? = null) = PaymentResponse(
    id = requireNotNull(id),
    reservationId = requireNotNull(reservation.id),
    status = status,
    provider = provider,
    providerReference = providerReference,
    orderId = orderId,
    createdAt = createdAt,
)

fun TicketOrder.toResponse() = OrderResponse(
    id = requireNotNull(id),
    reservationId = requireNotNull(reservation.id),
    eventName = reservation.ticketType.event.name,
    ticketTypeName = reservation.ticketType.name,
    quantity = reservation.quantity,
    totalAmount = totalAmount,
    createdAt = createdAt,
)
