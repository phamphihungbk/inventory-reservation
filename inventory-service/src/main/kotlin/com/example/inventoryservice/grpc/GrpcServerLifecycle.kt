package com.example.inventoryservice.grpc

import io.grpc.Server
import io.grpc.ServerBuilder
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component

@Component
@EnableConfigurationProperties(GrpcServerProperties::class)
class GrpcServerLifecycle(
    private val properties: GrpcServerProperties,
    private val inventoryGrpcService: InventoryGrpcService,
) {
    private lateinit var server: Server

    @PostConstruct
    fun start() {
        server = ServerBuilder
            .forPort(properties.port)
            .addService(inventoryGrpcService)
            .build()
            .start()
    }

    @PreDestroy
    fun stop() {
        server.shutdown()
    }
}
