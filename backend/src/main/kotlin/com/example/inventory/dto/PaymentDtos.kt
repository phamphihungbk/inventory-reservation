package com.example.inventory.dto

import com.example.inventory.entity.PaymentStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.Instant

data class CreatePaymentRequest(
    @field:NotNull
    val reservationId: Long,
    @field:NotBlank
    val cardNumber: String,
)

data class PaymentResponse(
    val id: Long,
    val reservationId: Long,
    val status: PaymentStatus,
    val provider: String,
    val providerReference: String,
    val orderId: Long?,
    val createdAt: Instant,
)

data class OrderResponse(
    val id: Long,
    val reservationId: Long,
    val eventName: String,
    val ticketTypeName: String,
    val quantity: Int,
    val totalAmount: BigDecimal,
    val createdAt: Instant,
)
