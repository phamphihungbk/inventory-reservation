package com.example.inventory.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "events")
class Event(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    var description: String,
    @Column(nullable = false)
    var venue: String,
    @Column(nullable = false)
    var city: String,
    @Column(nullable = false)
    var country: String,
    @Column(name = "event_date", nullable = false)
    var eventDate: Instant,
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val ticketTypes: MutableList<TicketType> = mutableListOf(),
)
