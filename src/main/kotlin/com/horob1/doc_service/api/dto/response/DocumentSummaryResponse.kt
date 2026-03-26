package com.horob1.doc_service.api.dto.response

import com.horob1.doc_service.shared.enums.FileType
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class DocumentSummaryResponse(
    val id: UUID,
    val title: String,
    val description: String,
    val fileType: FileType,
    val originalFileName: String,
    val fileSize: Long,
    val price: BigDecimal,
    val ownerId: UUID,
    val schoolName: String?,
    val departmentName: String?,
    val createdAt: Instant?,
)
