package com.horob1.doc_service.api.controller

import com.horob1.doc_service.api.dto.response.PurchaseResponse
import com.horob1.doc_service.service.query.PurchaseQueryHandler
import com.horob1.doc_service.shared.dto.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/purchases")
class PurchaseQueryController(
    private val purchaseQueryHandler: PurchaseQueryHandler
) {
    @GetMapping
    fun getMyPurchases(): ResponseEntity<ApiResponse<List<PurchaseResponse>>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as UUID

        val apiResponse = ApiResponse.success(
            message = "Get purchases successfully",
            status = HttpStatus.OK,
            data = purchaseQueryHandler.getMyPurchases(userId)
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }
}
