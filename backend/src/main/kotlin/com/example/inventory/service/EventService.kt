package com.example.inventory.service

import com.example.inventory.dto.CreateEventRequest
import com.example.inventory.dto.EventResponse
import com.example.inventory.dto.EventSearchResponse
import com.example.inventory.entity.Event
import com.example.inventory.entity.TicketType
import com.example.inventory.exception.EventNotFoundException
import com.example.inventory.mapper.toResponse
import com.example.inventory.repository.EventRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EventService(
    private val eventRepository: EventRepository,
) {
    @Transactional(readOnly = true)
    fun getAll(): List<EventResponse> =
        eventRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getById(id: Long): EventResponse =
        eventRepository.findById(id)
            .orElseThrow { EventNotFoundException(id) }
            .toResponse()

    @Transactional(readOnly = true)
    fun search(query: String, page: Int, size: Int): Page<EventSearchResponse> =
        eventRepository.search(
            query = query.trim(),
            pageable = PageRequest.of(page.coerceAtLeast(0), size.coerceIn(1, 50)),
        ).map { it.toResponse() }

    @Transactional
    fun create(request: CreateEventRequest): EventResponse {
        val event = Event(
            name = request.name.trim(),
            description = request.description.trim(),
            venue = request.venue.trim(),
            city = request.city.trim(),
            country = request.country.trim(),
            eventDate = request.eventDate,
        )

        request.ticketTypes.forEach { ticketType ->
            event.ticketTypes.add(
                TicketType(
                    event = event,
                    name = ticketType.name.trim(),
                    price = ticketType.price,
                    remainingQuantity = ticketType.remainingQuantity,
                ),
            )
        }

        return eventRepository.save(event).toResponse()
    }
}
