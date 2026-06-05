package com.example.inventory.exception

class EventNotFoundException(id: Long) : RuntimeException("Event $id not found")

class TicketTypeNotFoundException(id: Long) : RuntimeException("Ticket type $id not found")

class ReservationNotFoundException(id: Long) : RuntimeException("Reservation $id not found")

class PaymentNotFoundException(id: Long) : RuntimeException("Payment $id not found")

class OrderNotFoundException(id: Long) : RuntimeException("Order $id not found")

class InsufficientStockException(ticketTypeId: Long, requested: Int, available: Int) :
    RuntimeException("Insufficient tickets for ticket type $ticketTypeId: requested $requested, available $available")

class ReservationExpiredException(id: Long) : RuntimeException("Reservation $id is not active")

class DuplicatePaymentException(id: Long) : RuntimeException("Reservation $id already has a payment")
