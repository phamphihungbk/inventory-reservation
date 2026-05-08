package com.example.inventory.controller

import com.example.inventory.dto.CreateReservationRequest
import com.example.inventory.dto.ReservationResponse
import com.example.inventory.service.ReservationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/reservations")
class ReservationController(
    private val reservationService: ReservationService,
) {
    @GetMapping
    fun getAll(): ResponseEntity<List<ReservationResponse>> =
        ResponseEntity.ok(reservationService.getAll())

    @PostMapping
    fun create(@Valid @RequestBody request: CreateReservationRequest): ResponseEntity<ReservationResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(request))

    @DeleteMapping("/{id}")
    fun cancel(@PathVariable id: Long): ResponseEntity<ReservationResponse> =
        ResponseEntity.ok(reservationService.cancel(id))

    @PostMapping("/expire")
    fun expire(): ResponseEntity<Map<String, Int>> =
        ResponseEntity.ok(mapOf("expired" to reservationService.expireActiveReservations()))
}
