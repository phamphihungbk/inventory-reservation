package com.example.notification.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "sent_notifications")
class SentNotification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "event_id", nullable = false)
    val eventId: String,
    @Column(name = "event_type", nullable = false)
    val eventType: String,
    @Column(nullable = false)
    val recipient: String,
    @Column(nullable = false)
    val subject: String,
    @Column(nullable = false)
    val status: String,
    @Column(name = "sent_at", nullable = false)
    val sentAt: Instant = Instant.now(),
)
