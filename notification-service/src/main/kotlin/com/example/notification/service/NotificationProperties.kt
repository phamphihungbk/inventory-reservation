package com.example.notification.service

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "notification")
data class NotificationProperties(
    val recipient: String = "demo@example.com",
)
