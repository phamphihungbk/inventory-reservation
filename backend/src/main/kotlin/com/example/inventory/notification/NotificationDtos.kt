package com.example.inventory.notification

import java.time.Instant

data class NotificationDebugResponse(
    val id: Long,
    val eventId: String,
    val eventType: String,
    val recipient: String,
    val subject: String,
    val status: String,
    val sentAt: Instant,
)
