package com.horob1.doc_service.domain.repository

import com.horob1.doc_service.domain.model.purchase.Purchase
import com.horob1.doc_service.shared.enums.PurchaseStatus
import java.util.UUID

interface PurchaseRepository {
    fun findById(id: UUID): Purchase?
    fun findByUserId(userId: UUID): List<Purchase>
    fun findByUserIdAndDocumentId(userId: UUID, documentId: UUID): Purchase?
    fun save(purchase: Purchase): Purchase
    fun existsByUserIdAndDocumentIdAndStatus(userId: UUID, documentId: UUID, status: PurchaseStatus): Boolean
    fun findByPaymentCode(paymentCode: String): Purchase?
    fun existsByTransactionId(transactionId: String): Boolean
}
