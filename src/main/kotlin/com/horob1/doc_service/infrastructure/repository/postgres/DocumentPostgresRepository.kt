package com.horob1.doc_service.infrastructure.repository.postgres

import com.horob1.doc_service.domain.model.document.Document
import com.horob1.doc_service.shared.enums.DocumentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DocumentPostgresRepository : JpaRepository<Document, UUID> {
    fun findByOwnerId(ownerId: UUID): List<Document>
    fun findAllByStatus(status: DocumentStatus, pageable: Pageable): Page<Document>
}
