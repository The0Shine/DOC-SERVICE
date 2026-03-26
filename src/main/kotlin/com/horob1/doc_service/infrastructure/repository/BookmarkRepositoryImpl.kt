package com.horob1.doc_service.infrastructure.repository

import com.horob1.doc_service.domain.model.bookmark.Bookmark
import com.horob1.doc_service.domain.repository.BookmarkRepository
import com.horob1.doc_service.infrastructure.repository.postgres.BookmarkPostgresRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class BookmarkRepositoryImpl(
    private val bookmarkPostgresRepository: BookmarkPostgresRepository
) : BookmarkRepository {

    override fun findByUserIdAndDocumentId(userId: UUID, documentId: UUID): Bookmark? {
        return bookmarkPostgresRepository.findByUserIdAndDocumentId(userId, documentId)
    }

    override fun findByUserId(userId: UUID): List<Bookmark> {
        return bookmarkPostgresRepository.findByUserId(userId)
    }

    override fun save(bookmark: Bookmark): Bookmark {
        return bookmarkPostgresRepository.save(bookmark)
    }

    override fun delete(bookmark: Bookmark) {
        bookmarkPostgresRepository.delete(bookmark)
    }

    override fun existsByUserIdAndDocumentId(userId: UUID, documentId: UUID): Boolean {
        return bookmarkPostgresRepository.existsByUserIdAndDocumentId(userId, documentId)
    }
}
