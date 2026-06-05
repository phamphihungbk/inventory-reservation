package com.example.inventory.controller

import com.example.inventory.dto.CreatePaymentRequest
import com.example.inventory.dto.PaymentResponse
import com.example.inventory.service.PaymentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService,
) {
    @PostMapping
    fun create(@Valid @RequestBody request: CreatePaymentRequest): ResponseEntity<PaymentResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(paymentService.pay(request))
}
