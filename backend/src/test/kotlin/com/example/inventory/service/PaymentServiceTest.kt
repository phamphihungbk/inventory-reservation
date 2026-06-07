package com.example.inventory.service

import com.example.inventory.dto.CreatePaymentRequest
import com.example.inventory.entity.Event
import com.example.inventory.entity.Payment
import com.example.inventory.entity.PaymentStatus
import com.example.inventory.entity.Reservation
import com.example.inventory.entity.TicketOrder
import com.example.inventory.entity.TicketType
import com.example.inventory.exception.DuplicatePaymentException
import com.example.inventory.kafka.KafkaTopics
import com.example.inventory.outbox.OutboxEventService
import com.example.inventory.repository.PaymentRepository
import com.example.inventory.repository.ReservationRepository
import com.example.inventory.repository.TicketOrderRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.dao.DataIntegrityViolationException
import java.math.BigDecimal
import java.time.Instant
import java.util.Optional

class PaymentServiceTest {
    private val reservationRepository = mockk<ReservationRepository>()
    private val paymentRepository = mockk<PaymentRepository>()
    private val ticketOrderRepository = mockk<TicketOrderRepository>()
    private val paymentGateway = mockk<PaymentGateway>()
    private val sseService = mockk<TicketInventorySseService>()
    private val outboxEventService = mockk<OutboxEventService>()
    private val service = PaymentService(
        reservationRepository,
        paymentRepository,
        ticketOrderRepository,
        paymentGateway,
        sseService,
        outboxEventService,
    )

    @Test
    fun `successful payment creates payment order and outbox events`() {
        val reservation = reservation()
        every { reservationRepository.findById(10) } returns Optional.of(reservation)
        every { paymentRepository.saveAndFlush(any<Payment>()) } answers {
            val payment = firstArg<Payment>()
            Payment(
                id = 30,
                reservation = payment.reservation,
                status = payment.status,
                provider = payment.provider,
                providerReference = payment.providerReference,
                createdAt = payment.createdAt,
            )
        }
        coEvery { paymentGateway.charge("4242424242424242") } returns PaymentGatewayResult(
            succeeded = true,
            providerReference = "stripe-success-1",
        )
        every { ticketOrderRepository.save(any<TicketOrder>()) } answers {
            val order = firstArg<TicketOrder>()
            TicketOrder(
                id = 40,
                reservation = order.reservation,
                totalAmount = order.totalAmount,
                createdAt = order.createdAt,
            )
        }
        every { sseService.broadcast(reservation.ticketType) } just runs
        every { outboxEventService.enqueue(any(), any()) } just runs

        val response = service.pay(CreatePaymentRequest(10, "4242424242424242"))

        assertEquals(PaymentStatus.SUCCEEDED, response.status)
        assertEquals("stripe-success-1", response.providerReference)
        assertEquals(40, response.orderId)
        verify { outboxEventService.enqueue(KafkaTopics.PAYMENT_SUCCEEDED, any()) }
        verify { outboxEventService.enqueue(KafkaTopics.TICKET_PURCHASED, any()) }
    }

    @Test
    fun `duplicate payment returns conflict before charging card`() {
        val reservation = reservation()
        every { reservationRepository.findById(10) } returns Optional.of(reservation)
        every { paymentRepository.saveAndFlush(any<Payment>()) } throws DataIntegrityViolationException("duplicate")

        assertThrows(DuplicatePaymentException::class.java) {
            service.pay(CreatePaymentRequest(10, "4242424242424242"))
        }

        coVerify(exactly = 0) { paymentGateway.charge(any()) }
    }

    private fun reservation(): Reservation {
        val event = Event(
            id = 1,
            name = "Coldplay Vienna",
            description = "Live concert",
            venue = "Ernst Happel Stadion",
            city = "Vienna",
            country = "Austria",
            eventDate = Instant.parse("2026-08-01T19:00:00Z"),
        )
        val ticketType = TicketType(
            id = 1,
            event = event,
            name = "VIP",
            price = BigDecimal("299.00"),
            remainingQuantity = 10,
        )
        return Reservation(
            id = 10,
            ticketType = ticketType,
            quantity = 2,
            expiresAt = Instant.parse("2026-05-08T10:15:00Z"),
        )
    }
}
