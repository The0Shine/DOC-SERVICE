package com.horob1.doc_service.api.controller

import com.horob1.doc_service.api.dto.request.CreateDocumentDto
import com.horob1.doc_service.api.dto.request.UpdateDocumentDto
import com.horob1.doc_service.api.dto.response.DocumentResponse
import com.horob1.doc_service.service.command.DocumentCommandHandler
import com.horob1.doc_service.shared.dto.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/v1/documents")
class DocumentCommandController(
    private val documentCommandHandler: DocumentCommandHandler
) {
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun create(
        @RequestPart("file") file: MultipartFile,
        @Valid @RequestPart("metadata") dto: CreateDocumentDto,
    ): ResponseEntity<ApiResponse<DocumentResponse>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as UUID

        val apiResponse = ApiResponse.success(
            message = "Upload document successfully",
            status = HttpStatus.CREATED,
            data = documentCommandHandler.create(file, dto, userId)
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    @PutMapping("/{documentId}")
    fun update(
        @PathVariable documentId: String,
        @Valid @RequestBody dto: UpdateDocumentDto,
    ): ResponseEntity<ApiResponse<DocumentResponse>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as UUID

        val apiResponse = ApiResponse.success(
            message = "Update document successfully",
            status = HttpStatus.OK,
            data = documentCommandHandler.update(UUID.fromString(documentId), dto, userId)
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    @DeleteMapping("/{documentId}")
    fun delete(
        @PathVariable documentId: String,
    ): ResponseEntity<ApiResponse<Nothing>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as UUID

        documentCommandHandler.delete(UUID.fromString(documentId), userId)
        val apiResponse = ApiResponse.success<Nothing>(
            message = "Delete document successfully",
            status = HttpStatus.OK,
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }
}
