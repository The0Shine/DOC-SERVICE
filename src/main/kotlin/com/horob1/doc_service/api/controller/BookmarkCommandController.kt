package com.horob1.doc_service.api.controller

import com.horob1.doc_service.service.command.BookmarkCommandHandler
import com.horob1.doc_service.shared.dto.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/bookmarks")
class BookmarkCommandController(
    private val bookmarkCommandHandler: BookmarkCommandHandler
) {
    @PostMapping("/{documentId}")
    fun create(
        @PathVariable documentId: String
    ): ResponseEntity<ApiResponse<Nothing>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as UUID

        bookmarkCommandHandler.create(userId, UUID.fromString(documentId))
        val apiResponse = ApiResponse.success<Nothing>(
            message = "Bookmark added successfully",
            status = HttpStatus.CREATED,
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    @DeleteMapping("/{documentId}")
    fun delete(
        @PathVariable documentId: String
    ): ResponseEntity<ApiResponse<Nothing>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as UUID

        bookmarkCommandHandler.delete(userId, UUID.fromString(documentId))
        val apiResponse = ApiResponse.success<Nothing>(
            message = "Bookmark removed successfully",
            status = HttpStatus.OK,
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }
}
