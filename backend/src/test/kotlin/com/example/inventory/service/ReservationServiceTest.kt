package com.example.inventory.service

import com.example.inventory.config.ReservationProperties
import com.example.inventory.dto.CreateReservationRequest
import com.example.inventory.entity.Product
import com.example.inventory.entity.Reservation
import com.example.inventory.entity.ReservationStatus
import com.example.inventory.exception.InsufficientStockException
import com.example.inventory.repository.ProductRepository
import com.example.inventory.repository.ReservationRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.Optional

class ReservationServiceTest {
    private val productRepository = mockk<ProductRepository>()
    private val reservationRepository = mockk<ReservationRepository>()
    private val properties = ReservationProperties().apply { defaultTtlMinutes = 15 }
    private val clock = Clock.fixed(Instant.parse("2026-05-08T10:00:00Z"), ZoneOffset.UTC)
    private val service = ReservationService(productRepository, reservationRepository, properties, clock)

    @Test
    fun `create reservation reduces stock`() {
        val product = Product(id = 1, name = "Sneaker", stock = 5, price = BigDecimal("99.99"))
        every { productRepository.findById(1) } returns Optional.of(product)
        every { reservationRepository.save(any<Reservation>()) } answers {
            val reservation = firstArg<Reservation>()
            Reservation(
                id = 20,
                product = reservation.product,
                quantity = reservation.quantity,
                status = reservation.status,
                expiresAt = reservation.expiresAt,
                createdAt = reservation.createdAt,
            )
        }

        val response = service.create(CreateReservationRequest(productId = 1, quantity = 2))

        assertEquals(3, product.stock)
        assertEquals(2, response.quantity)
        assertEquals(ReservationStatus.ACTIVE, response.status)
    }

    @Test
    fun `create reservation rejects insufficient stock`() {
        val product = Product(id = 1, name = "Sneaker", stock = 1, price = BigDecimal("99.99"))
        every { productRepository.findById(1) } returns Optional.of(product)

        assertThrows(InsufficientStockException::class.java) {
            service.create(CreateReservationRequest(productId = 1, quantity = 2))
        }

        assertEquals(1, product.stock)
    }

    @Test
    fun `cancel active reservation restores stock`() {
        val product = Product(id = 1, name = "Sneaker", stock = 3, price = BigDecimal("99.99"))
        val reservation = Reservation(
            id = 10,
            product = product,
            quantity = 2,
            expiresAt = Instant.parse("2026-05-08T10:15:00Z"),
        )
        every { reservationRepository.findById(10) } returns Optional.of(reservation)

        val response = service.cancel(10)

        assertEquals(5, product.stock)
        assertEquals(ReservationStatus.CANCELED, response.status)
    }
}
