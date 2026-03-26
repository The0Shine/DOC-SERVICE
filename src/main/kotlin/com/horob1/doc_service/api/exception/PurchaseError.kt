package com.horob1.doc_service.api.exception

import com.horob1.doc_service.shared.exception.AppError
import org.springframework.http.HttpStatus

val AlreadyPurchased = AppError.CustomError(
    code = "PURCHASE_0001",
    message = "Document already purchased",
    status = HttpStatus.CONFLICT
)

val DocumentIsFree = AppError.CustomError(
    code = "PURCHASE_0002",
    message = "Document is free, no purchase needed",
    status = HttpStatus.BAD_REQUEST
)

val PurchaseNotFound = AppError.CustomError(
    code = "PURCHASE_0003",
    message = "Purchase not found",
    status = HttpStatus.NOT_FOUND
)

val CannotPurchaseOwnDocument = AppError.CustomError(
    code = "PURCHASE_0004",
    message = "Cannot purchase your own document",
    status = HttpStatus.BAD_REQUEST
)
