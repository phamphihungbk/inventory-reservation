package com.example.inventory

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class InventoryReservationApplication

fun main(args: Array<String>) {
    runApplication<InventoryReservationApplication>(*args)
}
