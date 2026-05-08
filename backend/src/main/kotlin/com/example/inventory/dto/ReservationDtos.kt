package com.example.inventory.dto

import com.example.inventory.entity.ReservationStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.Instant

data class CreateReservationRequest(
    @field:NotNull
    val productId: Long,

    @field:Min(1)
    val quantity: Int,
)

data class ReservationResponse(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val status: ReservationStatus,
    val expiresAt: Instant,
    val createdAt: Instant,
)
