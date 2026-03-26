package com.horob1.doc_service.service.command

import com.horob1.doc_service.api.dto.request.SepayWebhookDto
import com.horob1.doc_service.api.dto.response.PurchaseResponse
import com.horob1.doc_service.api.dto.response.toResponse
import com.horob1.doc_service.api.exception.*
import com.horob1.doc_service.config.SepayConfig
import com.horob1.doc_service.domain.model.purchase.Purchase
import com.horob1.doc_service.domain.repository.DocumentRepository
import com.horob1.doc_service.domain.repository.PurchaseRepository
import com.horob1.doc_service.infrastructure.producer.DocEventProducer
import com.horob1.doc_service.shared.enums.DocumentStatus
import com.horob1.doc_service.shared.enums.PurchaseStatus
import com.horob1.doc_service.shared.exception.AppException
import com.horob1.doc_service.shared.kafka.event.AccessGrantEvent
import com.horob1.doc_service.util.PaymentCodeGenerator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.UUID

@Service
class PurchaseCommandHandler(
    private val purchaseRepository: PurchaseRepository,
    private val documentRepository: DocumentRepository,
    private val docEventProducer: DocEventProducer,
    private val sepayConfig: SepayConfig,
) {
    private val logger = LoggerFactory.getLogger(PurchaseCommandHandler::class.java)

    companion object {
        private val PAYMENT_CODE_REGEX = Regex("DOCUBE[A-Z0-9]{8}")
    }

    fun create(userId: UUID, documentId: UUID): PurchaseResponse {
        val document = documentRepository.findById(documentId) ?: throw AppException(DocumentNotFound)

        if (document.status == DocumentStatus.DELETED) {
            throw AppException(DocumentAlreadyDeleted)
        }

        if (document.ownerId == userId) {
            throw AppException(CannotPurchaseOwnDocument)
        }

        if (document.price.compareTo(BigDecimal.ZERO) == 0) {
            throw AppException(DocumentIsFree)
        }

        if (purchaseRepository.existsByUserIdAndDocumentIdAndStatus(userId, documentId, PurchaseStatus.COMPLETED)) {
            throw AppException(AlreadyPurchased)
        }

        // If there's already a PENDING purchase, return it with QR URL
        val existingPending = purchaseRepository.findByUserIdAndDocumentId(userId, documentId)
        if (existingPending != null && existingPending.status == PurchaseStatus.PENDING) {
            val qrUrl = existingPending.paymentCode?.let { sepayConfig.buildQrUrl(existingPending.amount, it) }
            return existingPending.toResponse(qrUrl)
        }

        val paymentCode = PaymentCodeGenerator.generate()

        val purchase = purchaseRepository.save(
            Purchase(
                userId = userId,
                document = document,
                amount = document.price,
                status = PurchaseStatus.PENDING,
                paymentCode = paymentCode,
            )
        )

        val qrUrl = sepayConfig.buildQrUrl(purchase.amount, paymentCode)
        return purchase.toResponse(qrUrl)
    }

    @Transactional
    fun confirmPayment(webhook: SepayWebhookDto): Boolean {
        val transactionId = webhook.id.toString()

        // Idempotency: already processed this SePay transaction
        if (purchaseRepository.existsByTransactionId(transactionId)) {
            logger.info("[SEPAY] Duplicate webhook for transactionId={}", transactionId)
            return true
        }

        // Extract payment code from content
        val paymentCode = extractPaymentCode(webhook.content) ?: run {
            logger.warn("[SEPAY] No DOCUBE code found in content: {}", webhook.content)
            return false
        }

        val purchase = purchaseRepository.findByPaymentCode(paymentCode) ?: run {
            logger.warn("[SEPAY] No purchase found for paymentCode={}", paymentCode)
            return false
        }

        if (purchase.status != PurchaseStatus.PENDING) {
            logger.info("[SEPAY] Purchase {} already in status {}", purchase.id, purchase.status)
            return true
        }

        // Amount verification
        if (webhook.transferAmount.compareTo(purchase.amount) != 0) {
            logger.warn(
                "[SEPAY] Amount mismatch for purchase {}: expected={}, got={}",
                purchase.id, purchase.amount, webhook.transferAmount
            )
            return false
        }

        purchase.status = PurchaseStatus.COMPLETED
        purchase.transactionId = transactionId
        purchaseRepository.save(purchase)

        try {
            docEventProducer.sendAccessGrantEvent(
                AccessGrantEvent(
                    documentId = purchase.document.id.toString(),
                    granteeUserId = purchase.userId.toString(),
                    systemUserId = purchase.document.ownerId.toString(),
                )
            )
        } catch (e: Exception) {
            logger.warn("[KAFKA] Failed to send access.grant for purchase {}: {}", purchase.id, e.message)
        }

        logger.info("[SEPAY] Purchase {} completed via transactionId={}", purchase.id, transactionId)
        return true
    }

    private fun extractPaymentCode(content: String?): String? {
        if (content.isNullOrBlank()) return null
        return PAYMENT_CODE_REGEX.find(content.uppercase())?.value
    }
}
