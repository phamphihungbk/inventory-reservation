package com.example.inventory.controller

import com.example.inventory.dto.OrderResponse
import com.example.inventory.service.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderService: OrderService,
) {
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<OrderResponse> =
        ResponseEntity.ok(orderService.getById(id))
}
