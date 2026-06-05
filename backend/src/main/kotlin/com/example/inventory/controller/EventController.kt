package com.example.inventory.controller

import com.example.inventory.dto.CreateEventRequest
import com.example.inventory.dto.EventResponse
import com.example.inventory.dto.EventSearchResponse
import com.example.inventory.service.EventService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService,
) {
    @GetMapping
    fun getAll(): ResponseEntity<List<EventResponse>> =
        ResponseEntity.ok(eventService.getAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<EventResponse> =
        ResponseEntity.ok(eventService.getById(id))

    @GetMapping("/search")
    fun search(
        @RequestParam q: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<Page<EventSearchResponse>> =
        ResponseEntity.ok(eventService.search(q, page, size))

    @PostMapping
    fun create(@Valid @RequestBody request: CreateEventRequest): ResponseEntity<EventResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(request))
}
