package com.horob1.doc_service.service.query

import com.horob1.doc_service.api.dto.response.PurchaseResponse
import com.horob1.doc_service.api.dto.response.toResponse
import com.horob1.doc_service.domain.repository.PurchaseRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PurchaseQueryHandler(
    private val purchaseRepository: PurchaseRepository
) {
    fun getMyPurchases(userId: UUID): List<PurchaseResponse> {
        return purchaseRepository.findByUserId(userId).map { it.toResponse() }
    }
}
