package com.example.inventory.service

import com.example.inventory.dto.CreateProductRequest
import com.example.inventory.dto.ProductResponse
import com.example.inventory.dto.UpdateProductRequest
import com.example.inventory.exception.ProductNotFoundException
import com.example.inventory.mapper.toEntity
import com.example.inventory.mapper.toResponse
import com.example.inventory.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    @Transactional(readOnly = true)
    fun getAll(): List<ProductResponse> =
        productRepository.findAll().map { it.toResponse() }

    @Transactional(readOnly = true)
    fun getById(id: Long): ProductResponse =
        productRepository.findById(id)
            .orElseThrow { ProductNotFoundException(id) }
            .toResponse()

    @Transactional
    fun create(request: CreateProductRequest): ProductResponse =
        productRepository.save(request.toEntity()).toResponse()

    @Transactional
    fun update(id: Long, request: UpdateProductRequest): ProductResponse {
        val product = productRepository.findById(id)
            .orElseThrow { ProductNotFoundException(id) }

        product.name = request.name.trim()
        product.stock = request.stock
        product.price = request.price

        return product.toResponse()
    }

    @Transactional
    fun delete(id: Long) {
        if (!productRepository.existsById(id)) {
            throw ProductNotFoundException(id)
        }

        productRepository.deleteById(id)
    }
}
