package com.example.inventory.grpc

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "inventory.grpc")
data class InventoryGrpcProperties(
    val host: String = "localhost",
    val port: Int = 9090,
)
