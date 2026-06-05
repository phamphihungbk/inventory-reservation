package com.example.inventory.mapper

import com.example.inventory.dto.EventResponse
import com.example.inventory.dto.EventSearchResponse
import com.example.inventory.dto.TicketTypeResponse
import com.example.inventory.entity.Event
import com.example.inventory.entity.TicketType
import com.example.inventory.repository.EventSearchProjection

fun Event.toResponse() = EventResponse(
    id = requireNotNull(id),
    name = name,
    description = description,
    venue = venue,
    city = city,
    country = country,
    eventDate = eventDate,
    ticketTypes = ticketTypes.map { it.toResponse() },
    createdAt = createdAt,
)

fun TicketType.toResponse() = TicketTypeResponse(
    id = requireNotNull(id),
    eventId = requireNotNull(event.id),
    name = name,
    price = price,
    remainingQuantity = remainingQuantity,
    version = version,
)

fun EventSearchProjection.toResponse() = EventSearchResponse(
    id = getId(),
    name = getName(),
    venue = getVenue(),
    city = getCity(),
    country = getCountry(),
    eventDate = getEventDate(),
    rank = getRank(),
)
