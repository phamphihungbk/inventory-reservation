package com.example.inventoryservice.grpc

import com.example.inventoryservice.grpc.generated.GetAvailabilityRequest
import com.example.inventoryservice.grpc.generated.GetAvailabilityResponse
import com.example.inventoryservice.grpc.generated.InventoryServiceGrpc
import com.example.inventoryservice.grpc.generated.ReleaseTicketsRequest
import com.example.inventoryservice.grpc.generated.ReleaseTicketsResponse
import com.example.inventoryservice.grpc.generated.ReserveTicketsRequest
import com.example.inventoryservice.grpc.generated.ReserveTicketsResponse
import com.example.inventoryservice.repository.TicketTypeRepository
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InventoryGrpcService(
    private val ticketTypeRepository: TicketTypeRepository,
) : InventoryServiceGrpc.InventoryServiceImplBase() {
    @Transactional
    override fun reserveTickets(
        request: ReserveTicketsRequest,
        responseObserver: StreamObserver<ReserveTicketsResponse>,
    ) {
        respond(responseObserver) {
            val ticketType = findTicketType(request.ticketTypeId)

            if (ticketType.remainingQuantity < request.quantity) {
                return@respond ReserveTicketsResponse.newBuilder()
                    .setSuccess(false)
                    .setRemainingQuantity(ticketType.remainingQuantity)
                    .build()
            }

            ticketType.remainingQuantity -= request.quantity
            ReserveTicketsResponse.newBuilder()
                .setSuccess(true)
                .setRemainingQuantity(ticketType.remainingQuantity)
                .build()
        }
    }

    @Transactional
    override fun releaseTickets(
        request: ReleaseTicketsRequest,
        responseObserver: StreamObserver<ReleaseTicketsResponse>,
    ) {
        respond(responseObserver) {
            val ticketType = findTicketType(request.ticketTypeId)
            ticketType.remainingQuantity += request.quantity

            ReleaseTicketsResponse.newBuilder()
                .setSuccess(true)
                .setRemainingQuantity(ticketType.remainingQuantity)
                .build()
        }
    }

    @Transactional(readOnly = true)
    override fun getAvailability(
        request: GetAvailabilityRequest,
        responseObserver: StreamObserver<GetAvailabilityResponse>,
    ) {
        respond(responseObserver) {
            val ticketType = findTicketType(request.ticketTypeId)
            GetAvailabilityResponse.newBuilder()
                .setRemainingQuantity(ticketType.remainingQuantity)
                .build()
        }
    }

    private fun findTicketType(rawId: String) =
        ticketTypeRepository.findById(rawId.toLong())
            .orElseThrow { NoSuchElementException("Ticket type $rawId not found") }

    private fun <T> respond(responseObserver: StreamObserver<T>, block: () -> T) {
        try {
            responseObserver.onNext(block())
            responseObserver.onCompleted()
        } catch (ex: OptimisticLockingFailureException) {
            responseObserver.onError(Status.ABORTED.withDescription("Inventory version conflict").withCause(ex).asRuntimeException())
        } catch (ex: NoSuchElementException) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(ex.message).withCause(ex).asRuntimeException())
        } catch (ex: Exception) {
            responseObserver.onError(Status.INTERNAL.withDescription(ex.message).withCause(ex).asRuntimeException())
        }
    }
}
