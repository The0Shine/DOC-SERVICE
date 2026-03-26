package com.horob1.doc_service.infrastructure.repository.postgres

import com.horob1.doc_service.domain.model.bookmark.Bookmark
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface BookmarkPostgresRepository : JpaRepository<Bookmark, UUID> {
    fun findByUserIdAndDocumentId(userId: UUID, documentId: UUID): Bookmark?
    fun findByUserId(userId: UUID): List<Bookmark>
    fun existsByUserIdAndDocumentId(userId: UUID, documentId: UUID): Boolean
}
