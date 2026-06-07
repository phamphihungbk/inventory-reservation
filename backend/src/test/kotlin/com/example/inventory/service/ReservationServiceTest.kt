package com.example.inventory.service

import com.example.inventory.config.ReservationProperties
import com.example.inventory.dto.CreateReservationRequest
import com.example.inventory.entity.Event
import com.example.inventory.entity.Reservation
import com.example.inventory.entity.ReservationStatus
import com.example.inventory.entity.TicketType
import com.example.inventory.exception.InsufficientStockException
import com.example.inventory.grpc.InventoryChangeResult
import com.example.inventory.grpc.InventoryGrpcClient
import com.example.inventory.kafka.KafkaTopics
import com.example.inventory.outbox.OutboxEventService
import com.example.inventory.repository.ReservationRepository
import com.example.inventory.repository.TicketTypeRepository
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.transaction.support.TransactionTemplate
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.Optional

class ReservationServiceTest {
    private val ticketTypeRepository = mockk<TicketTypeRepository>()
    private val reservationRepository = mockk<ReservationRepository>()
    private val sseService = mockk<TicketInventorySseService>()
    private val inventoryGrpcClient = mockk<InventoryGrpcClient>()
    private val outboxEventService = mockk<OutboxEventService>()
    private val transactionTemplate = mockk<TransactionTemplate>()
    private val properties = ReservationProperties().apply { defaultTtlMinutes = 15 }
    private val clock = Clock.fixed(Instant.parse("2026-05-08T10:00:00Z"), ZoneOffset.UTC)
    private val service = ReservationService(
        ticketTypeRepository,
        reservationRepository,
        properties,
        sseService,
        inventoryGrpcClient,
        outboxEventService,
        transactionTemplate,
        clock,
    )

    @Test
    fun `create reservation reduces remaining ticket quantity`() {
        val ticketType = ticketType(remaining = 5)
        every { ticketTypeRepository.findById(1) } returns Optional.of(ticketType)
        every { inventoryGrpcClient.reserveTickets(1, 2) } returns InventoryChangeResult(1, 3)
        every { reservationRepository.save(any<Reservation>()) } answers {
            val reservation = firstArg<Reservation>()
            Reservation(
                id = 20,
                ticketType = reservation.ticketType,
                quantity = reservation.quantity,
                status = reservation.status,
                expiresAt = reservation.expiresAt,
                createdAt = reservation.createdAt,
            )
        }
        every { sseService.broadcastInventory(1, 3) } just runs
        every { outboxEventService.enqueue(any(), any()) } just runs

        val response = service.create(CreateReservationRequest(ticketTypeId = 1, quantity = 2))

        assertEquals(5, ticketType.remainingQuantity)
        assertEquals(2, response.quantity)
        assertEquals(ReservationStatus.ACTIVE, response.status)
        verify { inventoryGrpcClient.reserveTickets(1, 2) }
        verify { sseService.broadcastInventory(1, 3) }
        verify { outboxEventService.enqueue(KafkaTopics.RESERVATION_CREATED, any()) }
        verify { outboxEventService.enqueue(KafkaTopics.INVENTORY_CHANGED, any()) }
    }

    @Test
    fun `create reservation rejects insufficient tickets`() {
        val ticketType = ticketType(remaining = 1)
        every { ticketTypeRepository.findById(1) } returns Optional.of(ticketType)
        every { inventoryGrpcClient.reserveTickets(1, 2) } throws InsufficientStockException(1, 2, 1)

        assertThrows(InsufficientStockException::class.java) {
            service.create(CreateReservationRequest(ticketTypeId = 1, quantity = 2))
        }

        assertEquals(1, ticketType.remainingQuantity)
    }

    @Test
    fun `cancel active reservation restores remaining ticket quantity`() {
        val ticketType = ticketType(remaining = 3)
        val reservation = Reservation(
            id = 10,
            ticketType = ticketType,
            quantity = 2,
            expiresAt = Instant.parse("2026-05-08T10:15:00Z"),
        )
        every { reservationRepository.findById(10) } returns Optional.of(reservation)
        every { inventoryGrpcClient.releaseTickets(1, 2) } returns InventoryChangeResult(1, 5)
        every { sseService.broadcastInventory(1, 5) } just runs
        every { outboxEventService.enqueue(any(), any()) } just runs

        val response = service.cancel(10)

        assertEquals(3, ticketType.remainingQuantity)
        assertEquals(ReservationStatus.CANCELLED, response.status)
        verify { inventoryGrpcClient.releaseTickets(1, 2) }
        verify { sseService.broadcastInventory(1, 5) }
        verify { outboxEventService.enqueue(KafkaTopics.RESERVATION_CANCELLED, any()) }
        verify { outboxEventService.enqueue(KafkaTopics.INVENTORY_CHANGED, any()) }
    }

    private fun ticketType(remaining: Int): TicketType {
        val event = Event(
            id = 1,
            name = "Coldplay Vienna",
            description = "Live concert",
            venue = "Ernst Happel Stadion",
            city = "Vienna",
            country = "Austria",
            eventDate = Instant.parse("2026-08-01T19:00:00Z"),
        )
        return TicketType(
            id = 1,
            event = event,
            name = "General Admission",
            price = BigDecimal("99.99"),
            remainingQuantity = remaining,
        )
    }
}
