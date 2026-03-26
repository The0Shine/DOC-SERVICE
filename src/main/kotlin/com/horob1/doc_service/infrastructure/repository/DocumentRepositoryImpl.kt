package com.horob1.doc_service.infrastructure.repository

import com.horob1.doc_service.domain.model.document.Document
import com.horob1.doc_service.domain.repository.DocumentRepository
import com.horob1.doc_service.infrastructure.repository.postgres.DocumentPostgresRepository
import com.horob1.doc_service.shared.enums.DocumentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class DocumentRepositoryImpl(
    private val documentPostgresRepository: DocumentPostgresRepository
) : DocumentRepository {

    override fun findById(id: UUID): Document? {
        return documentPostgresRepository.findById(id).orElse(null)
    }

    override fun findByOwnerId(ownerId: UUID): List<Document> {
        return documentPostgresRepository.findByOwnerId(ownerId)
    }

    override fun findAllByStatus(status: DocumentStatus, pageable: Pageable): Page<Document> {
        return documentPostgresRepository.findAllByStatus(status, pageable)
    }

    override fun save(document: Document): Document {
        return documentPostgresRepository.save(document)
    }

    override fun delete(document: Document) {
        documentPostgresRepository.delete(document)
    }
}
