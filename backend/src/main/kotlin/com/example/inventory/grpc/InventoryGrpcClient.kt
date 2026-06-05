package com.example.inventory.grpc

import com.example.inventory.exception.InsufficientStockException
import com.example.inventory.exception.TicketTypeNotFoundException
import com.example.inventory.grpc.generated.GetAvailabilityRequest
import com.example.inventory.grpc.generated.InventoryServiceGrpc
import com.example.inventory.grpc.generated.ReleaseTicketsRequest
import com.example.inventory.grpc.generated.ReserveTicketsRequest
import io.grpc.Status
import io.grpc.StatusRuntimeException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Component

data class InventoryChangeResult(
    val ticketTypeId: Long,
    val remainingQuantity: Int,
)

@Component
class InventoryGrpcClient(
    private val inventoryService: InventoryServiceGrpc.InventoryServiceBlockingStub,
) {
    fun reserveTickets(ticketTypeId: Long, quantity: Int): InventoryChangeResult {
        val response = callGrpc {
            inventoryService.reserveTickets(
                ReserveTicketsRequest.newBuilder()
                    .setTicketTypeId(ticketTypeId.toString())
                    .setQuantity(quantity)
                    .build(),
            )
        }

        if (!response.success) {
            throw InsufficientStockException(ticketTypeId, quantity, response.remainingQuantity)
        }

        return InventoryChangeResult(ticketTypeId, response.remainingQuantity)
    }

    fun releaseTickets(ticketTypeId: Long, quantity: Int): InventoryChangeResult {
        val response = callGrpc {
            inventoryService.releaseTickets(
                ReleaseTicketsRequest.newBuilder()
                    .setTicketTypeId(ticketTypeId.toString())
                    .setQuantity(quantity)
                    .build(),
            )
        }

        return InventoryChangeResult(ticketTypeId, response.remainingQuantity)
    }

    fun getAvailability(ticketTypeId: Long): Int =
        callGrpc {
            inventoryService.getAvailability(
                GetAvailabilityRequest.newBuilder()
                    .setTicketTypeId(ticketTypeId.toString())
                    .build(),
            )
        }.remainingQuantity

    private fun <T> callGrpc(block: () -> T): T =
        try {
            block()
        } catch (ex: StatusRuntimeException) {
            when (ex.status.code) {
                Status.Code.ABORTED -> throw OptimisticLockingFailureException(ex.status.description ?: "Inventory version conflict", ex)
                Status.Code.NOT_FOUND -> throw TicketTypeNotFoundException(ex.status.description?.substringAfter("Ticket type ")?.substringBefore(" ")?.toLongOrNull() ?: -1)
                else -> throw ex
            }
        }
}
