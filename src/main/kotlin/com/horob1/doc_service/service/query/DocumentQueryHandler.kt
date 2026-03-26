package com.horob1.doc_service.service.query

import com.horob1.doc_service.api.dto.response.DocumentResponse
import com.horob1.doc_service.api.dto.response.DocumentSummaryResponse
import com.horob1.doc_service.api.exception.DocumentAccessDenied
import com.horob1.doc_service.api.exception.DocumentAlreadyDeleted
import com.horob1.doc_service.api.exception.DocumentNotFound
import com.horob1.doc_service.domain.model.document.toResponse
import com.horob1.doc_service.domain.model.document.toSummary
import com.horob1.doc_service.domain.repository.DocumentRepository
import com.horob1.doc_service.domain.repository.PurchaseRepository
import com.horob1.doc_service.infrastructure.minio.MinioStorageService
import com.horob1.doc_service.shared.enums.DocumentStatus
import com.horob1.doc_service.shared.enums.PurchaseStatus
import com.horob1.doc_service.shared.exception.AppException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class DocumentQueryHandler(
    private val documentRepository: DocumentRepository,
    private val purchaseRepository: PurchaseRepository,
    private val minioStorageService: MinioStorageService,
) {
    fun getById(id: UUID): DocumentResponse {
        val document = documentRepository.findById(id) ?: throw AppException(DocumentNotFound)
        if (document.status == DocumentStatus.DELETED) {
            throw AppException(DocumentNotFound)
        }
        return document.toResponse()
    }

    fun getAll(pageable: Pageable): Page<DocumentSummaryResponse> {
        return documentRepository.findAllByStatus(DocumentStatus.ACTIVE, pageable)
            .map { it.toSummary() }
    }

    fun getMyDocuments(ownerId: UUID): List<DocumentResponse> {
        return documentRepository.findByOwnerId(ownerId).map { it.toResponse() }
    }

    fun getDownloadUrl(documentId: UUID, userId: UUID): String {
        val document = documentRepository.findById(documentId) ?: throw AppException(DocumentNotFound)

        if (document.status == DocumentStatus.DELETED) {
            throw AppException(DocumentAlreadyDeleted)
        }

        // Access check: owner OR purchased OR free document
        val hasAccess = document.ownerId == userId
                || document.price.compareTo(BigDecimal.ZERO) == 0
                || purchaseRepository.existsByUserIdAndDocumentIdAndStatus(
            userId, documentId, PurchaseStatus.COMPLETED
        )

        if (!hasAccess) {
            throw AppException(DocumentAccessDenied)
        }

        return minioStorageService.getPresignedUrl(
            key = document.minioKey,
            bucket = document.minioBucket,
        )
    }
}
