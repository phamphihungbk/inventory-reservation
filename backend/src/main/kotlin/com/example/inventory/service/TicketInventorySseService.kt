package com.example.inventory.service

import com.example.inventory.entity.TicketType
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Service
class TicketInventorySseService {
    private val subscribers = ConcurrentHashMap<Long, CopyOnWriteArrayList<SseEmitter>>()

    fun watch(ticketTypeId: Long): SseEmitter {
        val emitter = SseEmitter(0L)
        subscribers.computeIfAbsent(ticketTypeId) { CopyOnWriteArrayList() }.add(emitter)
        emitter.onCompletion { remove(ticketTypeId, emitter) }
        emitter.onTimeout { remove(ticketTypeId, emitter) }
        emitter.onError { remove(ticketTypeId, emitter) }
        send(emitter, "connected", mapOf("ticketTypeId" to ticketTypeId))
        return emitter
    }

    fun broadcast(ticketType: TicketType) {
        val ticketTypeId = requireNotNull(ticketType.id)
        broadcastInventory(ticketTypeId, ticketType.remainingQuantity, ticketType.version)
    }

    fun broadcastInventory(ticketTypeId: Long, remainingQuantity: Int, version: Long? = null) {
        val payload = mapOf(
            "ticketTypeId" to ticketTypeId,
            "remainingQuantity" to remainingQuantity,
            "version" to version,
        )
        subscribers[ticketTypeId]?.forEach { send(it, "inventory-updated", payload) }
    }

    private fun remove(ticketTypeId: Long, emitter: SseEmitter) {
        subscribers[ticketTypeId]?.remove(emitter)
    }

    private fun send(emitter: SseEmitter, eventName: String, data: Any) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data))
        } catch (_: IOException) {
            emitter.complete()
        } catch (_: IllegalStateException) {
            emitter.complete()
        } catch (_: RuntimeException) {
            emitter.complete()
        }
    }
}
