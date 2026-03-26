package com.horob1.doc_service.api.dto.response

import com.horob1.doc_service.shared.enums.DocumentStatus
import com.horob1.doc_service.shared.enums.FileType
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class DocumentResponse(
    val id: UUID,
    val title: String,
    val description: String,
    val fileType: FileType,
    val originalFileName: String,
    val fileSize: Long,
    val docHash: String,
    val hashAlgo: String,
    val blockchainDocId: String?,
    val status: DocumentStatus,
    val price: BigDecimal,
    val ownerId: UUID,
    val schoolId: UUID?,
    val schoolName: String?,
    val departmentId: UUID?,
    val departmentName: String?,
    val createdAt: Instant?,
    val updatedAt: Instant?,
)
