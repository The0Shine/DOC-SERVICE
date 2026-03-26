package com.horob1.doc_service.domain.repository

import com.horob1.doc_service.domain.model.document.Document
import com.horob1.doc_service.shared.enums.DocumentStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface DocumentRepository {
    fun findById(id: UUID): Document?
    fun findByOwnerId(ownerId: UUID): List<Document>
    fun findAllByStatus(status: DocumentStatus, pageable: Pageable): Page<Document>
    fun save(document: Document): Document
    fun delete(document: Document)
}
