package com.example.inventory.repository

import com.example.inventory.entity.TicketOrder
import org.springframework.data.jpa.repository.JpaRepository

interface TicketOrderRepository : JpaRepository<TicketOrder, Long> {
    fun findByReservation_Id(reservationId: Long): TicketOrder?
}
