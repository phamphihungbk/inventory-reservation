package com.example.inventory.service

interface PaymentGateway {
    suspend fun charge(cardNumber: String): PaymentGatewayResult
}

data class PaymentGatewayResult(
    val succeeded: Boolean,
    val providerReference: String,
)
