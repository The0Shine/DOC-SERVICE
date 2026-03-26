package com.horob1.doc_service.api.exception

import com.horob1.doc_service.shared.exception.AppError
import org.springframework.http.HttpStatus

val DocumentNotFound = AppError.CustomError(
    code = "DOC_0001",
    message = "Document not found",
    status = HttpStatus.NOT_FOUND
)

val DocumentAlreadyDeleted = AppError.CustomError(
    code = "DOC_0002",
    message = "Document is already deleted",
    status = HttpStatus.BAD_REQUEST
)

val InvalidFileType = AppError.CustomError(
    code = "DOC_0003",
    message = "Only Word (.docx), PDF (.pdf), and Text (.txt) files are supported",
    status = HttpStatus.BAD_REQUEST
)

val FileTooLarge = AppError.CustomError(
    code = "DOC_0004",
    message = "File size exceeds maximum limit",
    status = HttpStatus.BAD_REQUEST
)

val NotDocumentOwner = AppError.CustomError(
    code = "DOC_0005",
    message = "You are not the owner of this document",
    status = HttpStatus.FORBIDDEN
)

val DocumentAccessDenied = AppError.CustomError(
    code = "DOC_0006",
    message = "You don't have access to this document",
    status = HttpStatus.FORBIDDEN
)

val FileUploadFailed = AppError.CustomError(
    code = "DOC_0007",
    message = "Failed to upload file",
    status = HttpStatus.INTERNAL_SERVER_ERROR
)
