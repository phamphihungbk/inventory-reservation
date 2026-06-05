package com.example.inventory.service

import com.example.inventory.dto.OrderResponse
import com.example.inventory.exception.OrderNotFoundException
import com.example.inventory.mapper.toResponse
import com.example.inventory.repository.TicketOrderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val ticketOrderRepository: TicketOrderRepository,
) {
    @Transactional(readOnly = true)
    fun getById(id: Long): OrderResponse =
        ticketOrderRepository.findById(id)
            .orElseThrow { OrderNotFoundException(id) }
            .toResponse()
}
