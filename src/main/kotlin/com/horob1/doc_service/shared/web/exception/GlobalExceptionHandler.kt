package com.horob1.doc_service.shared.web.exception

import com.horob1.doc_service.shared.dto.response.ApiResponse
import com.horob1.doc_service.shared.exception.AppError
import com.horob1.doc_service.shared.exception.AppException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(AppException::class)
    fun handleAppException(e: AppException): ResponseEntity<ApiResponse<Nothing>> {
        val error = e.appError
        logger.error("AppException occurred: code={}, message='{}'", error.code, error.message, e)

        val response = ApiResponse.error<Nothing>(
            status = error.status,
            message = error.message,
            code = error.code
        )
        return ResponseEntity.status(error.status).body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val error = AppError.ValidationError
        val errorMessage = e.bindingResult.fieldErrors
            .joinToString("; ") { "'${it.field}': ${it.defaultMessage}" }

        logger.warn("Validation error: {}", errorMessage)

        val response = ApiResponse.error<Nothing>(
            status = error.status,
            message = errorMessage,
            code = error.code
        )
        return ResponseEntity.status(error.status).body(response)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException): ResponseEntity<ApiResponse<Nothing>> {
        val error = AppError.AccessForbidden
        logger.warn("Access denied: {}", e.message)

        val response = ApiResponse.error<Nothing>(
            status = error.status,
            code = error.code,
            message = error.message
        )
        return ResponseEntity.status(error.status).body(response)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(e: AuthenticationException): ResponseEntity<ApiResponse<Nothing>> {
        val error = AppError.AuthenticationFailed
        logger.warn("Authentication failed: {}", e.message)

        val response = ApiResponse.error<Nothing>(
            status = error.status,
            code = error.code,
            message = error.message
        )
        return ResponseEntity.status(error.status).body(response)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(e: HttpRequestMethodNotSupportedException): ResponseEntity<ApiResponse<Nothing>> {
        val error = AppError.MethodNotAllowed
        val message =
            "Method '${e.method}' is not supported for this route. Supported methods are ${e.supportedHttpMethods}."
        logger.warn(message)

        val response = ApiResponse.error<Nothing>(
            status = error.status,
            code = error.code,
            message = message
        )
        return ResponseEntity.status(error.status).body(response)
    }

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFound(e: NoHandlerFoundException): ResponseEntity<ApiResponse<Nothing>> {
        val error = AppError.NotFound
        logger.warn("No handler found for {} {}", e.httpMethod, e.requestURL)
        val response = ApiResponse.error<Nothing>(
            status = error.status,
            code = error.code,
            message = "API route not found: ${e.requestURL}"
        )
        return ResponseEntity.status(error.status).body(response)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        val error = AppError.InternalServerError
        logger.error("An unexpected error occurred", e)

        val response = ApiResponse.error<Nothing>(
            status = error.status,
            message = error.message,
            code = error.code
        )
        return ResponseEntity.status(error.status).body(response)
    }
}
