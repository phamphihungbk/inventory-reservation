package com.example.inventory.dto

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import java.time.Instant

data class CreateProductRequest(
    @field:NotBlank
    val name: String,

    @field:Min(0)
    val stock: Int,

    @field:DecimalMin("0.00")
    val price: BigDecimal,
)

data class UpdateProductRequest(
    @field:NotBlank
    val name: String,

    @field:Min(0)
    val stock: Int,

    @field:DecimalMin("0.00")
    val price: BigDecimal,
)

data class ProductResponse(
    val id: Long,
    val name: String,
    val stock: Int,
    val price: BigDecimal,
    val version: Long,
    val createdAt: Instant,
)
