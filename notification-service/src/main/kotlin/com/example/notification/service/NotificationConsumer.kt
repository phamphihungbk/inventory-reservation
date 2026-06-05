package com.example.notification.service

import com.example.notification.entity.ProcessedEvent
import com.example.notification.entity.SentNotification
import com.example.notification.repository.ProcessedEventRepository
import com.example.notification.repository.SentNotificationRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@EnableConfigurationProperties(NotificationProperties::class)
class NotificationConsumer(
    private val objectMapper: ObjectMapper,
    private val mailSender: JavaMailSender,
    private val properties: NotificationProperties,
    private val processedEventRepository: ProcessedEventRepository,
    private val sentNotificationRepository: SentNotificationRepository,
) {
    @KafkaListener(
        topics = [
            "reservation.created",
            "reservation.expired",
            "ticket.purchased",
            "payment.failed",
        ],
    )
    @Transactional
    fun consume(payload: String) {
        val event = objectMapper.readValue(payload, TicketEvent::class.java)
        if (processedEventRepository.existsById(event.eventId)) {
            return
        }

        val message = messageFor(event)
        val mail = SimpleMailMessage().apply {
            setTo(properties.recipient)
            from = "ticketmaster-mvp@example.com"
            subject = message.subject
            text = message.body
        }

        mailSender.send(mail)
        processedEventRepository.save(ProcessedEvent(event.eventId))
        sentNotificationRepository.save(
            SentNotification(
                eventId = event.eventId,
                eventType = event.eventType,
                recipient = properties.recipient,
                subject = message.subject,
                status = "SENT",
            ),
        )
    }

    private fun messageFor(event: TicketEvent): NotificationMessage =
        when (event.eventType) {
            "reservation.created.v1" -> NotificationMessage(
                subject = "Reservation Created",
                body = "Your tickets have been reserved.\nReservation ID: ${event.reservationId}\nReservation expires in 15 minutes.",
            )
            "reservation.expired.v1" -> NotificationMessage(
                subject = "Reservation Expired",
                body = "Your reservation has expired and inventory has been released.",
            )
            "ticket.purchased.v1" -> NotificationMessage(
                subject = "Purchase Confirmed",
                body = "Your order has been confirmed.\nOrder ID: ${event.orderId}\nTickets: ${event.quantity}",
            )
            "payment.failed.v1" -> NotificationMessage(
                subject = "Payment Failed",
                body = "Your payment could not be processed. Please try again.",
            )
            else -> NotificationMessage(
                subject = "Ticket Event",
                body = "Event received: ${event.eventType}",
            )
        }
}

data class NotificationMessage(
    val subject: String,
    val body: String,
)
