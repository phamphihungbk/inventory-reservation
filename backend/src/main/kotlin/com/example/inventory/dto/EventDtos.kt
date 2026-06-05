package com.example.inventory.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import java.math.BigDecimal
import java.time.Instant

data class CreateTicketTypeRequest(
    @field:NotBlank
    val name: String,
    @field:DecimalMin("0.00")
    val price: BigDecimal,
    @field:Min(0)
    val remainingQuantity: Int,
)

data class CreateEventRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val description: String,
    @field:NotBlank
    val venue: String,
    @field:NotBlank
    val city: String,
    @field:NotBlank
    val country: String,
    @field:Future
    val eventDate: Instant,
    @field:NotEmpty
    val ticketTypes: List<@Valid CreateTicketTypeRequest>,
)

data class TicketTypeResponse(
    val id: Long,
    val eventId: Long,
    val name: String,
    val price: BigDecimal,
    val remainingQuantity: Int,
    val version: Long,
)

data class EventResponse(
    val id: Long,
    val name: String,
    val description: String,
    val venue: String,
    val city: String,
    val country: String,
    val eventDate: Instant,
    val ticketTypes: List<TicketTypeResponse>,
    val createdAt: Instant,
)

data class EventSearchResponse(
    val id: Long,
    val name: String,
    val venue: String,
    val city: String,
    val country: String,
    val eventDate: Instant,
    val rank: Double,
)
