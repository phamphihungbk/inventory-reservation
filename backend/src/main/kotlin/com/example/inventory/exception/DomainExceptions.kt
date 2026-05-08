package com.example.inventory.exception

class ProductNotFoundException(id: Long) : RuntimeException("Product $id not found")

class ReservationNotFoundException(id: Long) : RuntimeException("Reservation $id not found")

class InsufficientStockException(productId: Long, requested: Int, available: Int) :
    RuntimeException("Insufficient stock for product $productId: requested $requested, available $available")
