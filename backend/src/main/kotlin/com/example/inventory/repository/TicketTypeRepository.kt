package com.example.inventory.repository

import com.example.inventory.entity.TicketType
import org.springframework.data.jpa.repository.JpaRepository

interface TicketTypeRepository : JpaRepository<TicketType, Long>
