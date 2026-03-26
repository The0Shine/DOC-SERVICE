package com.horob1.doc_service.api.exception

import com.horob1.doc_service.shared.exception.AppError
import org.springframework.http.HttpStatus

val BookmarkAlreadyExists = AppError.CustomError(
    code = "BOOKMARK_0001",
    message = "Document already bookmarked",
    status = HttpStatus.CONFLICT
)

val BookmarkNotFound = AppError.CustomError(
    code = "BOOKMARK_0002",
    message = "Bookmark not found",
    status = HttpStatus.NOT_FOUND
)
