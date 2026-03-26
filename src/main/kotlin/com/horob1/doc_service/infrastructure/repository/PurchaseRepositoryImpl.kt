package com.horob1.doc_service.infrastructure.repository

import com.horob1.doc_service.domain.model.purchase.Purchase
import com.horob1.doc_service.domain.repository.PurchaseRepository
import com.horob1.doc_service.infrastructure.repository.postgres.PurchasePostgresRepository
import com.horob1.doc_service.shared.enums.PurchaseStatus
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class PurchaseRepositoryImpl(
    private val purchasePostgresRepository: PurchasePostgresRepository
) : PurchaseRepository {

    override fun findById(id: UUID): Purchase? {
        return purchasePostgresRepository.findById(id).orElse(null)
    }

    override fun findByUserId(userId: UUID): List<Purchase> {
        return purchasePostgresRepository.findByUserId(userId)
    }

    override fun findByUserIdAndDocumentId(userId: UUID, documentId: UUID): Purchase? {
        return purchasePostgresRepository.findByUserIdAndDocumentId(userId, documentId)
    }

    override fun save(purchase: Purchase): Purchase {
        return purchasePostgresRepository.save(purchase)
    }

    override fun existsByUserIdAndDocumentIdAndStatus(userId: UUID, documentId: UUID, status: PurchaseStatus): Boolean {
        return purchasePostgresRepository.existsByUserIdAndDocumentIdAndStatus(userId, documentId, status)
    }

    override fun findByPaymentCode(paymentCode: String): Purchase? {
        return purchasePostgresRepository.findByPaymentCode(paymentCode)
    }

    override fun existsByTransactionId(transactionId: String): Boolean {
        return purchasePostgresRepository.existsByTransactionId(transactionId)
    }
}
