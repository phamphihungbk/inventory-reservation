package com.example.inventory.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.math.BigDecimal

@Entity
@Table(name = "ticket_types")
class TicketType(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    val event: Event,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false, precision = 12, scale = 2)
    var price: BigDecimal,
    @Column(name = "remaining_quantity", nullable = false)
    var remainingQuantity: Int,
    @Version
    @Column(nullable = false)
    var version: Long = 0,
)
