package com.example.inventory.mapper

import com.example.inventory.dto.CreateProductRequest
import com.example.inventory.dto.ProductResponse
import com.example.inventory.entity.Product

fun CreateProductRequest.toEntity() = Product(
    name = name.trim(),
    stock = stock,
    price = price,
)

fun Product.toResponse() = ProductResponse(
    id = requireNotNull(id),
    name = name,
    stock = stock,
    price = price,
    version = version,
    createdAt = createdAt,
)
