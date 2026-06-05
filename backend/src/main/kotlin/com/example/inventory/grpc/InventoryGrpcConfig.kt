package com.example.inventory.grpc

import com.example.inventory.grpc.generated.InventoryServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(InventoryGrpcProperties::class)
class InventoryGrpcConfig {
    @Bean
    fun inventoryGrpcChannel(properties: InventoryGrpcProperties): ManagedChannel =
        ManagedChannelBuilder
            .forAddress(properties.host, properties.port)
            .usePlaintext()
            .build()

    @Bean
    fun inventoryGrpcStub(channel: ManagedChannel): InventoryServiceGrpc.InventoryServiceBlockingStub =
        InventoryServiceGrpc.newBlockingStub(channel)
}
