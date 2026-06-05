package com.example.inventoryservice.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version

@Entity
@Table(name = "ticket_types")
class TicketType(
    @Id
    val id: Long? = null,
    @Column(name = "event_id", nullable = false)
    val eventId: Long,
    @Column(nullable = false)
    val name: String,
    @Column(name = "remaining_quantity", nullable = false)
    var remainingQuantity: Int,
    @Version
    @Column(nullable = false)
    var version: Long = 0,
)
