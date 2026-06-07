package com.example.inventory.service

import com.example.inventory.entity.TicketType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Service
class TicketInventorySseService {
    private val subscribers = ConcurrentHashMap<Long, CopyOnWriteArrayList<SseEmitter>>()

    fun watch(ticketTypeId: Long): SseEmitter {
        val emitter = SseEmitter(EMITTER_TIMEOUT_MS)
        subscribers.computeIfAbsent(ticketTypeId) { CopyOnWriteArrayList() }.add(emitter)
        emitter.onCompletion { remove(ticketTypeId, emitter) }
        emitter.onTimeout { remove(ticketTypeId, emitter) }
        emitter.onError { remove(ticketTypeId, emitter) }
        send(ticketTypeId, emitter, "connected", mapOf("ticketTypeId" to ticketTypeId))
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
        subscribers[ticketTypeId]?.forEach { send(ticketTypeId, it, "inventory-updated", payload) }
    }

    @Scheduled(fixedRateString = "\${sse.heartbeat-ms:25000}")
    fun sendHeartbeat() {
        subscribers.forEach { (ticketTypeId, emitters) ->
            emitters.forEach {
                send(ticketTypeId, it, "heartbeat", mapOf("ticketTypeId" to ticketTypeId))
            }
        }
    }

    private fun remove(ticketTypeId: Long, emitter: SseEmitter) {
        val emitters = subscribers[ticketTypeId] ?: return
        emitters.remove(emitter)
        if (emitters.isEmpty()) {
            subscribers.remove(ticketTypeId, emitters)
        }
    }

    private fun send(ticketTypeId: Long, emitter: SseEmitter, eventName: String, data: Any) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data))
        } catch (_: IOException) {
            remove(ticketTypeId, emitter)
            emitter.complete()
        } catch (_: IllegalStateException) {
            remove(ticketTypeId, emitter)
            emitter.complete()
        } catch (_: RuntimeException) {
            remove(ticketTypeId, emitter)
            emitter.complete()
        }
    }

    private companion object {
        const val EMITTER_TIMEOUT_MS = 30L * 60L * 1000L
    }
}
