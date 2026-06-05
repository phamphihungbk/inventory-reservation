package com.example.notification.repository

import com.example.notification.entity.SentNotification
import org.springframework.data.jpa.repository.JpaRepository

interface SentNotificationRepository : JpaRepository<SentNotification, Long>
