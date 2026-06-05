package com.example.inventory.repository

import java.time.Instant

interface EventSearchProjection {
    fun getId(): Long
    fun getName(): String
    fun getVenue(): String
    fun getCity(): String
    fun getCountry(): String
    fun getEventDate(): Instant
    fun getRank(): Double
}
