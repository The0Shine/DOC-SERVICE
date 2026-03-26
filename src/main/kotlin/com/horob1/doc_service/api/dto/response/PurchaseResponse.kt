package com.horob1.doc_service.api.dto.response

import com.horob1.doc_service.domain.model.purchase.Purchase
import com.horob1.doc_service.shared.enums.PurchaseStatus
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class PurchaseResponse(
    val id: UUID,
    val userId: UUID,
    val documentId: UUID,
    val documentTitle: String,
    val amount: BigDecimal,
    val status: PurchaseStatus,
    val transactionId: String?,
    val paymentCode: String?,
    val qrUrl: String?,
    val createdAt: Instant?,
    val updatedAt: Instant?,
)

fun Purchase.toResponse(qrUrl: String? = null) = PurchaseResponse(
    id = this.id!!,
    userId = this.userId,
    documentId = this.document.id!!,
    documentTitle = this.document.title,
    amount = this.amount,
    status = this.status,
    transactionId = this.transactionId,
    paymentCode = this.paymentCode,
    qrUrl = qrUrl,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)
