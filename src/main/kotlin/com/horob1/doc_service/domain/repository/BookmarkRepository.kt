package com.horob1.doc_service.domain.repository

import com.horob1.doc_service.domain.model.bookmark.Bookmark
import java.util.UUID

interface BookmarkRepository {
    fun findByUserIdAndDocumentId(userId: UUID, documentId: UUID): Bookmark?
    fun findByUserId(userId: UUID): List<Bookmark>
    fun save(bookmark: Bookmark): Bookmark
    fun delete(bookmark: Bookmark)
    fun existsByUserIdAndDocumentId(userId: UUID, documentId: UUID): Boolean
}
