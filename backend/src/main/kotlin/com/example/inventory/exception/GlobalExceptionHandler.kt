package com.example.inventory.exception

import jakarta.validation.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.http.converter.HttpMessageNotReadableException

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ProductNotFoundException::class, ReservationNotFoundException::class)
    fun handleNotFound(ex: RuntimeException): ResponseEntity<ApiError> =
        error(HttpStatus.NOT_FOUND, ex.message ?: "Resource not found")

    @ExceptionHandler(InsufficientStockException::class, OptimisticLockingFailureException::class)
    fun handleConflict(ex: RuntimeException): ResponseEntity<ApiError> =
        error(HttpStatus.CONFLICT, ex.message ?: "Conflict")

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrity(ex: DataIntegrityViolationException): ResponseEntity<ApiError> =
        error(HttpStatus.CONFLICT, "Request conflicts with existing data")

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val message = ex.bindingResult.fieldErrors
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
            .ifBlank { "Validation failed" }

        return error(HttpStatus.BAD_REQUEST, message)
    }

    @ExceptionHandler(ConstraintViolationException::class, IllegalStateException::class)
    fun handleBadRequest(ex: RuntimeException): ResponseEntity<ApiError> =
        error(HttpStatus.BAD_REQUEST, ex.message ?: "Bad request")

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableBody(ex: HttpMessageNotReadableException): ResponseEntity<ApiError> =
        error(HttpStatus.BAD_REQUEST, "Request body is missing or invalid")

    private fun error(status: HttpStatus, message: String): ResponseEntity<ApiError> =
        ResponseEntity.status(status).body(
            ApiError(
                status = status.value(),
                error = status.reasonPhrase,
                message = message,
            )
        )
}
