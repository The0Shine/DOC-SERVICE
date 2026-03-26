package com.horob1.doc_service.service.command

import com.horob1.doc_service.api.exception.BookmarkAlreadyExists
import com.horob1.doc_service.api.exception.BookmarkNotFound
import com.horob1.doc_service.api.exception.DocumentNotFound
import com.horob1.doc_service.domain.model.bookmark.Bookmark
import com.horob1.doc_service.domain.repository.BookmarkRepository
import com.horob1.doc_service.domain.repository.DocumentRepository
import com.horob1.doc_service.shared.exception.AppException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BookmarkCommandHandler(
    private val bookmarkRepository: BookmarkRepository,
    private val documentRepository: DocumentRepository,
) {
    fun create(userId: UUID, documentId: UUID) {
        val document = documentRepository.findById(documentId) ?: throw AppException(DocumentNotFound)

        if (bookmarkRepository.existsByUserIdAndDocumentId(userId, documentId)) {
            throw AppException(BookmarkAlreadyExists)
        }

        bookmarkRepository.save(
            Bookmark(
                userId = userId,
                document = document,
            )
        )
    }

    fun delete(userId: UUID, documentId: UUID) {
        val bookmark = bookmarkRepository.findByUserIdAndDocumentId(userId, documentId)
            ?: throw AppException(BookmarkNotFound)

        bookmarkRepository.delete(bookmark)
    }
}
