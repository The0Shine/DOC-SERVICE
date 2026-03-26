package com.horob1.doc_service.service.query

import com.horob1.doc_service.api.dto.response.DocumentSummaryResponse
import com.horob1.doc_service.domain.model.document.toSummary
import com.horob1.doc_service.domain.repository.BookmarkRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BookmarkQueryHandler(
    private val bookmarkRepository: BookmarkRepository
) {
    fun getMyBookmarks(userId: UUID): List<DocumentSummaryResponse> {
        return bookmarkRepository.findByUserId(userId).map { it.document.toSummary() }
    }
}
