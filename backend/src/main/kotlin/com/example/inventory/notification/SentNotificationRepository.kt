package com.example.inventory.notification

import org.springframework.data.jpa.repository.JpaRepository

interface SentNotificationRepository : JpaRepository<SentNotification, Long> {
    fun findTop50ByOrderBySentAtDesc(): List<SentNotification>
}
