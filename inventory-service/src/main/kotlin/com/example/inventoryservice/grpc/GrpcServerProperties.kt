package com.example.inventoryservice.grpc

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "grpc.server")
data class GrpcServerProperties(
    val port: Int = 9090,
)
