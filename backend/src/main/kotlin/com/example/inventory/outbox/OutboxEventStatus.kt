package com.example.inventory.outbox

enum class OutboxEventStatus {
    PENDING,
    FAILED,
    PUBLISHED,
}
