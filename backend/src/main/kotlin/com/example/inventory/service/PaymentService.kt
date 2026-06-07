package com.example.inventory.service

import com.example.inventory.dto.CreatePaymentRequest
import com.example.inventory.dto.PaymentResponse
import com.example.inventory.entity.Payment
import com.example.inventory.entity.PaymentStatus
import com.example.inventory.entity.ReservationStatus
import com.example.inventory.entity.TicketOrder
import com.example.inventory.exception.DuplicatePaymentException
import com.example.inventory.exception.ReservationExpiredException
import com.example.inventory.exception.ReservationNotFoundException
import com.example.inventory.kafka.KafkaTopics
import com.example.inventory.kafka.TicketEvent
import com.example.inventory.mapper.toResponse
import com.example.inventory.outbox.OutboxEventService
import com.example.inventory.repository.PaymentRepository
import com.example.inventory.repository.ReservationRepository
import com.example.inventory.repository.TicketOrderRepository
import kotlinx.coroutines.runBlocking
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val reservationRepository: ReservationRepository,
    private val paymentRepository: PaymentRepository,
    private val ticketOrderRepository: TicketOrderRepository,
    private val paymentGateway: PaymentGateway,
    private val ticketInventorySseService: TicketInventorySseService,
    private val outboxEventService: OutboxEventService,
) {
    @Transactional
    fun pay(request: CreatePaymentRequest): PaymentResponse {
        val reservation = reservationRepository.findById(request.reservationId)
            .orElseThrow { ReservationNotFoundException(request.reservationId) }

        if (reservation.status != ReservationStatus.ACTIVE) {
            throw ReservationExpiredException(request.reservationId)
        }

        val payment = try {
            paymentRepository.saveAndFlush(
                Payment(
                    reservation = reservation,
                    status = PaymentStatus.PENDING,
                    provider = "stripe-simulator",
                    providerReference = "pending",
                ),
            )
        } catch (ex: DataIntegrityViolationException) {
            throw DuplicatePaymentException(request.reservationId)
        }

        val result = runBlocking { paymentGateway.charge(request.cardNumber) }
        val status = if (result.succeeded) PaymentStatus.SUCCEEDED else PaymentStatus.FAILED
        payment.status = status
        payment.providerReference = result.providerReference

        val order = if (status == PaymentStatus.SUCCEEDED) {
            ticketOrderRepository.save(
                TicketOrder(
                    reservation = reservation,
                    totalAmount = reservation.ticketType.price.multiply(reservation.quantity.toBigDecimal()),
                ),
            )
        } else {
            null
        }

        ticketInventorySseService.broadcast(reservation.ticketType)
        val ticketTypeId = requireNotNull(reservation.ticketType.id).toString()
        val reservationId = requireNotNull(reservation.id).toString()
        val paymentId = requireNotNull(payment.id).toString()

        outboxEventService.enqueue(
            if (status == PaymentStatus.SUCCEEDED) KafkaTopics.PAYMENT_SUCCEEDED else KafkaTopics.PAYMENT_FAILED,
            TicketEvent(
                eventType = if (status == PaymentStatus.SUCCEEDED) "payment.succeeded.v1" else "payment.failed.v1",
                reservationId = reservationId,
                ticketTypeId = ticketTypeId,
                quantity = reservation.quantity,
                paymentId = paymentId,
            ),
        )

        if (order != null) {
            outboxEventService.enqueue(
                KafkaTopics.TICKET_PURCHASED,
                TicketEvent(
                    eventType = "ticket.purchased.v1",
                    reservationId = reservationId,
                    ticketTypeId = ticketTypeId,
                    quantity = reservation.quantity,
                    paymentId = paymentId,
                    orderId = requireNotNull(order.id).toString(),
                    totalAmount = order.totalAmount,
                ),
            )
        }

        return payment.toResponse(orderId = order?.id)
    }
}
