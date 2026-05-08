package com.example.inventory.repository

import com.example.inventory.entity.Reservation
import com.example.inventory.entity.ReservationStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface ReservationRepository : JpaRepository<Reservation, Long> {
    fun findByStatusAndExpiresAtBefore(status: ReservationStatus, expiresAt: Instant): List<Reservation>
}
