package com.horob1.doc_service.api.exception

import com.horob1.doc_service.shared.exception.AppError
import org.springframework.http.HttpStatus

val DepartmentNotFound = AppError.CustomError(
    code = "DEPT_0001",
    message = "Department not found",
    status = HttpStatus.NOT_FOUND
)

val DepartmentAlreadyExists = AppError.CustomError(
    code = "DEPT_0002",
    message = "Department already exists in this school",
    status = HttpStatus.CONFLICT
)
