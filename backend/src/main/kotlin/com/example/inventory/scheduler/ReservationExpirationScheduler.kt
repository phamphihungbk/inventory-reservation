package com.example.inventory.scheduler

import com.example.inventory.service.ReservationService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ReservationExpirationScheduler(
    private val reservationService: ReservationService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedRateString = "PT1M")
    fun expireReservations() {
        val expired = reservationService.expireActiveReservations()
        if (expired > 0) {
            log.info("Expired {} reservations", expired)
        }
    }
}
