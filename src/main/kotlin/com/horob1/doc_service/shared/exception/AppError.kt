package com.horob1.doc_service.shared.exception

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class AppError(
    val code: String,
    val message: String,
    val status: HttpStatusCode,
) {
    data object InternalServerError : AppError(
        code = "SERVER_0001",
        message = "An error occurred on the server, please try again later.",
        status = HttpStatus.INTERNAL_SERVER_ERROR,
    )

    data object NotFound : AppError(
        code = "SERVER_0002",
        message = "The resource you requested does not exist.",
        status = HttpStatus.NOT_FOUND,
    )

    data object MethodNotAllowed : AppError(
        code = "SERVER_0003",
        message = "Method not supported.",
        status = HttpStatus.METHOD_NOT_ALLOWED,
    )

    data object AccessForbidden : AppError(
        code = "AUTH_0001",
        message = "You do not have permission to access this resource.",
        status = HttpStatus.FORBIDDEN,
    )

    data object AuthenticationFailed : AppError(
        code = "AUTH_0002",
        message = "Authentication failed.",
        status = HttpStatus.UNAUTHORIZED,
    )

    data object ValidationError : AppError(
        code = "VALIDATION_0001",
        message = "Invalid input data.",
        status = HttpStatus.UNPROCESSABLE_ENTITY,
    )

    class CustomError(
        code: String,
        message: String,
        status: HttpStatusCode,
    ) : AppError(code, message, status)
}
