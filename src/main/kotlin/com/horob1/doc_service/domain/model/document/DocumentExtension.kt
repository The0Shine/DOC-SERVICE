package com.horob1.doc_service.domain.model.document

import com.horob1.doc_service.api.dto.response.DocumentResponse
import com.horob1.doc_service.api.dto.response.DocumentSummaryResponse

fun Document.toResponse() = DocumentResponse(
    id = this.id!!,
    title = this.title,
    description = this.description,
    fileType = this.fileType,
    originalFileName = this.originalFileName,
    fileSize = this.fileSize,
    docHash = this.docHash,
    hashAlgo = this.hashAlgo,
    blockchainDocId = this.blockchainDocId,
    status = this.status,
    price = this.price,
    ownerId = this.ownerId,
    schoolId = this.school?.id,
    schoolName = this.school?.name,
    departmentId = this.department?.id,
    departmentName = this.department?.name,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun Document.toSummary() = DocumentSummaryResponse(
    id = this.id!!,
    title = this.title,
    description = this.description,
    fileType = this.fileType,
    originalFileName = this.originalFileName,
    fileSize = this.fileSize,
    price = this.price,
    ownerId = this.ownerId,
    schoolName = this.school?.name,
    departmentName = this.department?.name,
    createdAt = this.createdAt,
)
