package com.example.inventory.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "reservation")
class ReservationProperties {
    var defaultTtlMinutes: Long = 15
}
