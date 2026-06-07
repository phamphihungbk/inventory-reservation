package com.example.inventory.service

import com.example.inventory.config.ReservationProperties
import com.example.inventory.dto.CreateReservationRequest
import com.example.inventory.dto.ReservationResponse
import com.example.inventory.entity.Reservation
import com.example.inventory.entity.ReservationStatus
import com.example.inventory.exception.ReservationNotFoundException
import com.example.inventory.exception.TicketTypeNotFoundException
import com.example.inventory.grpc.InventoryGrpcClient
import com.example.inventory.kafka.KafkaTopics
import com.example.inventory.kafka.TicketEvent
import com.example.inventory.mapper.toResponse
import com.example.inventory.outbox.OutboxEventService
import com.example.inventory.repository.ReservationRepository
import com.example.inventory.repository.TicketTypeRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class ReservationService(
    private val ticketTypeRepository: TicketTypeRepository,
    private val reservationRepository: ReservationRepository,
    private val reservationProperties: ReservationProperties,
    private val ticketInventorySseService: TicketInventorySseService,
    private val inventoryGrpcClient: InventoryGrpcClient,
    private val outboxEventService: OutboxEventService,
    private val transactionTemplate: TransactionTemplate,
    private val clock: Clock,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true)
    fun getAll(): List<ReservationResponse> =
        reservationRepository.findAll().map { it.toResponse() }

    @Transactional
    fun create(request: CreateReservationRequest): ReservationResponse {
        val ticketType = ticketTypeRepository.findById(request.ticketTypeId)
            .orElseThrow { TicketTypeNotFoundException(request.ticketTypeId) }

        val inventory = inventoryGrpcClient.reserveTickets(request.ticketTypeId, request.quantity)

        val reservation = reservationRepository.save(
            Reservation(
                ticketType = ticketType,
                quantity = request.quantity,
                expiresAt = Instant.now(clock).plus(reservationProperties.defaultTtlMinutes, ChronoUnit.MINUTES),
            ),
        )

        ticketInventorySseService.broadcastInventory(request.ticketTypeId, inventory.remainingQuantity)
        publishInventoryChanged(request.ticketTypeId, request.quantity)
        outboxEventService.enqueue(
            KafkaTopics.RESERVATION_CREATED,
            TicketEvent(
                eventType = "reservation.created.v1",
                reservationId = requireNotNull(reservation.id).toString(),
                ticketTypeId = request.ticketTypeId.toString(),
                quantity = request.quantity,
            ),
        )

        return reservation.toResponse()
    }

    @Transactional
    fun cancel(id: Long): ReservationResponse {
        val reservation = reservationRepository.findById(id)
            .orElseThrow { ReservationNotFoundException(id) }

        if (reservation.status != ReservationStatus.ACTIVE) {
            return reservation.toResponse()
        }

        val ticketTypeId = requireNotNull(reservation.ticketType.id)
        val inventory = inventoryGrpcClient.releaseTickets(ticketTypeId, reservation.quantity)
        reservation.status = ReservationStatus.CANCELLED
        ticketInventorySseService.broadcastInventory(ticketTypeId, inventory.remainingQuantity)
        publishInventoryChanged(ticketTypeId, reservation.quantity)
        outboxEventService.enqueue(
            KafkaTopics.RESERVATION_CANCELLED,
            TicketEvent(
                eventType = "reservation.cancelled.v1",
                reservationId = requireNotNull(reservation.id).toString(),
                ticketTypeId = ticketTypeId.toString(),
                quantity = reservation.quantity,
            ),
        )

        return reservation.toResponse()
    }

    fun expireActiveReservations(): Int {
        val expiredReservations = reservationRepository.findByStatusAndExpiresAtBefore(
            status = ReservationStatus.ACTIVE,
            expiresAt = Instant.now(clock),
        )

        var expiredCount = 0
        expiredReservations.forEach { reservation ->
            val reservationId = requireNotNull(reservation.id)
            var ticketTypeId: Long? = null
            try {
                transactionTemplate.execute<Unit> {
                    val activeReservation = reservationRepository.findById(reservationId)
                        .orElseThrow { ReservationNotFoundException(reservationId) }
                    if (activeReservation.status != ReservationStatus.ACTIVE) {
                        return@execute
                    }

                    ticketTypeId = requireNotNull(activeReservation.ticketType.id)
                    val inventory = inventoryGrpcClient.releaseTickets(ticketTypeId!!, activeReservation.quantity)
                    activeReservation.status = ReservationStatus.EXPIRED
                    ticketInventorySseService.broadcastInventory(ticketTypeId!!, inventory.remainingQuantity)
                    publishInventoryChanged(ticketTypeId!!, activeReservation.quantity)
                    outboxEventService.enqueue(
                        KafkaTopics.RESERVATION_EXPIRED,
                        TicketEvent(
                            eventType = "reservation.expired.v1",
                            reservationId = reservationId.toString(),
                            ticketTypeId = ticketTypeId.toString(),
                            quantity = activeReservation.quantity,
                        ),
                    )
                    expiredCount += 1
                }
            } catch (ex: RuntimeException) {
                log.warn(
                    "Failed to expire reservation {} ticket type {}",
                    reservationId,
                    ticketTypeId ?: "unknown",
                    ex,
                )
            }
        }

        return expiredCount
    }

    private fun publishInventoryChanged(ticketTypeId: Long, quantity: Int) {
        outboxEventService.enqueue(
            KafkaTopics.INVENTORY_CHANGED,
            TicketEvent(
                eventType = "inventory.changed.v1",
                ticketTypeId = ticketTypeId.toString(),
                quantity = quantity,
            ),
        )
    }
}
