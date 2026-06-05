package com.example.inventory.notification

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/notifications")
class NotificationDebugController(
    private val repository: SentNotificationRepository,
) {
    @GetMapping
    fun getRecent(): ResponseEntity<List<NotificationDebugResponse>> =
        ResponseEntity.ok(
            repository.findTop50ByOrderBySentAtDesc().map {
                NotificationDebugResponse(
                    id = requireNotNull(it.id),
                    eventId = it.eventId,
                    eventType = it.eventType,
                    recipient = it.recipient,
                    subject = it.subject,
                    status = it.status,
                    sentAt = it.sentAt,
                )
            },
        )
}
