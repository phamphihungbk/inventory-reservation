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
import com.example.inventory.kafka.TicketEventPublisher
import com.example.inventory.mapper.toResponse
import com.example.inventory.repository.ReservationRepository
import com.example.inventory.repository.TicketTypeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
    private val ticketEventPublisher: TicketEventPublisher,
    private val clock: Clock,
) {
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
        ticketEventPublisher.publish(
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
        ticketEventPublisher.publish(
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

    @Transactional
    fun expireActiveReservations(): Int {
        val expiredReservations = reservationRepository.findByStatusAndExpiresAtBefore(
            status = ReservationStatus.ACTIVE,
            expiresAt = Instant.now(clock),
        )

        expiredReservations.forEach { reservation ->
            val ticketTypeId = requireNotNull(reservation.ticketType.id)
            val inventory = inventoryGrpcClient.releaseTickets(ticketTypeId, reservation.quantity)
            reservation.status = ReservationStatus.EXPIRED
            ticketInventorySseService.broadcastInventory(ticketTypeId, inventory.remainingQuantity)
            publishInventoryChanged(ticketTypeId, reservation.quantity)
            ticketEventPublisher.publish(
                KafkaTopics.RESERVATION_EXPIRED,
                TicketEvent(
                    eventType = "reservation.expired.v1",
                    reservationId = requireNotNull(reservation.id).toString(),
                    ticketTypeId = ticketTypeId.toString(),
                    quantity = reservation.quantity,
                ),
            )
        }

        return expiredReservations.size
    }

    private fun publishInventoryChanged(ticketTypeId: Long, quantity: Int) {
        ticketEventPublisher.publish(
            KafkaTopics.INVENTORY_CHANGED,
            TicketEvent(
                eventType = "inventory.changed.v1",
                ticketTypeId = ticketTypeId.toString(),
                quantity = quantity,
            ),
        )
    }
}
