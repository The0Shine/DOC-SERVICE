package com.horob1.doc_service.api.exception

import com.horob1.doc_service.shared.exception.AppError
import org.springframework.http.HttpStatus

val SchoolNotFound = AppError.CustomError(
    code = "SCHOOL_0001",
    message = "School not found",
    status = HttpStatus.NOT_FOUND
)

val SchoolAlreadyExists = AppError.CustomError(
    code = "SCHOOL_0002",
    message = "School already exists",
    status = HttpStatus.CONFLICT
)
