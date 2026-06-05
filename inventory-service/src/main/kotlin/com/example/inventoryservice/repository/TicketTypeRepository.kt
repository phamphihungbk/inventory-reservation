package com.example.inventoryservice.repository

import com.example.inventoryservice.entity.TicketType
import org.springframework.data.jpa.repository.JpaRepository

interface TicketTypeRepository : JpaRepository<TicketType, Long>
