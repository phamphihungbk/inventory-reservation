package com.example.inventory.service

import kotlinx.coroutines.delay
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.random.Random

@Component
class StripeSimulatorGateway : PaymentGateway {
    override suspend fun charge(cardNumber: String): PaymentGatewayResult {
        delay(Random.nextLong(1_000, 3_001))
        return PaymentGatewayResult(
            succeeded = cardNumber == SUCCESS_CARD,
            providerReference = "sim_${UUID.randomUUID()}",
        )
    }

    private companion object {
        const val SUCCESS_CARD = "4242424242424242"
    }
}
