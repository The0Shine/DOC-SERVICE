package com.horob1.doc_service.api.controller

import com.horob1.doc_service.api.dto.response.DocumentResponse
import com.horob1.doc_service.api.dto.response.DocumentSummaryResponse
import com.horob1.doc_service.service.query.DocumentQueryHandler
import com.horob1.doc_service.shared.dto.response.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/documents")
class DocumentQueryController(
    private val documentQueryHandler: DocumentQueryHandler
) {
    @GetMapping
    fun getAll(
        @PageableDefault(size = 20, sort = ["createdAt"]) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<DocumentSummaryResponse>>> {
        val apiResponse = ApiResponse.success(
            message = "Get documents successfully",
            status = HttpStatus.OK,
            data = documentQueryHandler.getAll(pageable)
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    @GetMapping("/{documentId}")
    fun getById(
        @PathVariable documentId: String
    ): ResponseEntity<ApiResponse<DocumentResponse>> {
        val apiResponse = ApiResponse.success(
            message = "Get document successfully",
            status = HttpStatus.OK,
            data = documentQueryHandler.getById(UUID.fromString(documentId))
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    @GetMapping("/my")
    fun getMyDocuments(): ResponseEntity<ApiResponse<List<DocumentResponse>>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as UUID

        val apiResponse = ApiResponse.success(
            message = "Get my documents successfully",
            status = HttpStatus.OK,
            data = documentQueryHandler.getMyDocuments(userId)
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    @GetMapping("/{documentId}/download")
    fun getDownloadUrl(
        @PathVariable documentId: String
    ): ResponseEntity<ApiResponse<Map<String, String>>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as UUID

        val url = documentQueryHandler.getDownloadUrl(UUID.fromString(documentId), userId)
        val apiResponse = ApiResponse.success(
            message = "Get download URL successfully",
            status = HttpStatus.OK,
            data = mapOf("downloadUrl" to url)
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }
}
