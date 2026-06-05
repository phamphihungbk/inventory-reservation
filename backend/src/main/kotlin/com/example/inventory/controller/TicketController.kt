package com.example.inventory.controller

import com.example.inventory.service.TicketInventorySseService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
@RequestMapping("/api/tickets")
class TicketController(
    private val ticketInventorySseService: TicketInventorySseService,
) {
    @GetMapping("/{id}/watch", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun watch(@PathVariable id: Long): SseEmitter =
        ticketInventorySseService.watch(id)
}
