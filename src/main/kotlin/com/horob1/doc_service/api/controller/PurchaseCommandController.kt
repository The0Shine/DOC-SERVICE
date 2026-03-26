package com.horob1.doc_service.api.controller

import com.horob1.doc_service.api.dto.request.CreatePurchaseDto
import com.horob1.doc_service.api.dto.response.PurchaseResponse
import com.horob1.doc_service.service.command.PurchaseCommandHandler
import com.horob1.doc_service.shared.dto.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/purchases")
class PurchaseCommandController(
    private val purchaseCommandHandler: PurchaseCommandHandler
) {
    @PostMapping
    fun create(
        @Valid @RequestBody dto: CreatePurchaseDto
    ): ResponseEntity<ApiResponse<PurchaseResponse>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as UUID

        val apiResponse = ApiResponse.success(
            message = "Purchase created successfully",
            status = HttpStatus.CREATED,
            data = purchaseCommandHandler.create(userId, dto.documentId)
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }
}
