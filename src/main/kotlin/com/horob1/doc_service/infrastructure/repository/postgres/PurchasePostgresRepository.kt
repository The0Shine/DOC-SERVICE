package com.horob1.doc_service.infrastructure.repository.postgres

import com.horob1.doc_service.domain.model.purchase.Purchase
import com.horob1.doc_service.shared.enums.PurchaseStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PurchasePostgresRepository : JpaRepository<Purchase, UUID> {
    fun findByUserId(userId: UUID): List<Purchase>
    fun findByUserIdAndDocumentId(userId: UUID, documentId: UUID): Purchase?
    fun existsByUserIdAndDocumentIdAndStatus(userId: UUID, documentId: UUID, status: PurchaseStatus): Boolean
    fun findByPaymentCode(paymentCode: String): Purchase?
    fun existsByTransactionId(transactionId: String): Boolean
}
