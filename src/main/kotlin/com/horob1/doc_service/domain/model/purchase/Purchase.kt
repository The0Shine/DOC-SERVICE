package com.horob1.doc_service.domain.model.purchase

import com.horob1.doc_service.domain.model.AbstractEntity
import com.horob1.doc_service.domain.model.document.Document
import com.horob1.doc_service.shared.enums.PurchaseStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(
    name = "tbl_purchases",
    indexes = [
        Index(name = "idx_purchase_user", columnList = "user_id"),
        Index(name = "idx_purchase_status", columnList = "status"),
        Index(name = "idx_purchase_user_doc", columnList = "user_id, document_id"),
        Index(name = "idx_purchase_payment_code", columnList = "payment_code", unique = true),
    ]
)
class Purchase(
    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    var document: Document,

    @Column(nullable = false)
    var amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: PurchaseStatus = PurchaseStatus.PENDING,

    @Column(name = "payment_code")
    var paymentCode: String? = null,

    @Column
    var transactionId: String? = null,
) : AbstractEntity<UUID>()
