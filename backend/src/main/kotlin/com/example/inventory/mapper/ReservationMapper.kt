package com.example.inventory.mapper

import com.example.inventory.dto.ReservationResponse
import com.example.inventory.entity.Reservation

fun Reservation.toResponse() = ReservationResponse(
    id = requireNotNull(id),
    productId = requireNotNull(product.id),
    quantity = quantity,
    status = status,
    expiresAt = expiresAt,
    createdAt = createdAt,
)
