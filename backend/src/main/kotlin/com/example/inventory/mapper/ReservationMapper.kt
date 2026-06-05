package com.example.inventory.mapper

import com.example.inventory.dto.ReservationResponse
import com.example.inventory.entity.Reservation

fun Reservation.toResponse() = ReservationResponse(
    id = requireNotNull(id),
    ticketTypeId = requireNotNull(ticketType.id),
    ticketTypeName = ticketType.name,
    eventId = requireNotNull(ticketType.event.id),
    eventName = ticketType.event.name,
    quantity = quantity,
    status = status,
    expiresAt = expiresAt,
    createdAt = createdAt,
)
