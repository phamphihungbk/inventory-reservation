package com.example.inventory.service

import com.example.inventory.config.ReservationProperties
import com.example.inventory.dto.CreateReservationRequest
import com.example.inventory.dto.ReservationResponse
import com.example.inventory.entity.Reservation
import com.example.inventory.entity.ReservationStatus
import com.example.inventory.exception.InsufficientStockException
import com.example.inventory.exception.ProductNotFoundException
import com.example.inventory.exception.ReservationNotFoundException
import com.example.inventory.mapper.toResponse
import com.example.inventory.repository.ProductRepository
import com.example.inventory.repository.ReservationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class ReservationService(
    private val productRepository: ProductRepository,
    private val reservationRepository: ReservationRepository,
    private val reservationProperties: ReservationProperties,
    private val clock: Clock,
) {
    @Transactional(readOnly = true)
    fun getAll(): List<ReservationResponse> =
        reservationRepository.findAll().map { it.toResponse() }

    @Transactional
    fun create(request: CreateReservationRequest): ReservationResponse {
        val product = productRepository.findById(request.productId)
            .orElseThrow { ProductNotFoundException(request.productId) }

        if (product.stock < request.quantity) {
            throw InsufficientStockException(
                productId = request.productId,
                requested = request.quantity,
                available = product.stock,
            )
        }

        product.stock -= request.quantity

        val reservation = Reservation(
            product = product,
            quantity = request.quantity,
            expiresAt = Instant.now(clock).plus(reservationProperties.defaultTtlMinutes, ChronoUnit.MINUTES),
        )

        return reservationRepository.save(reservation).toResponse()
    }

    @Transactional
    fun cancel(id: Long): ReservationResponse {
        val reservation = reservationRepository.findById(id)
            .orElseThrow { ReservationNotFoundException(id) }

        if (reservation.status != ReservationStatus.ACTIVE) {
            return reservation.toResponse()
        }

        reservation.product.stock += reservation.quantity
        reservation.status = ReservationStatus.CANCELED

        return reservation.toResponse()
    }

    @Transactional
    fun expireActiveReservations(): Int {
        val expiredReservations = reservationRepository.findByStatusAndExpiresAtBefore(
            status = ReservationStatus.ACTIVE,
            expiresAt = Instant.now(clock),
        )

        expiredReservations.forEach { reservation ->
            reservation.product.stock += reservation.quantity
            reservation.status = ReservationStatus.EXPIRED
        }

        return expiredReservations.size
    }
}
