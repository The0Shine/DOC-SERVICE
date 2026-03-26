package com.horob1.doc_service.domain.model.bookmark

import com.horob1.doc_service.domain.model.AbstractEntity
import com.horob1.doc_service.domain.model.document.Document
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
    name = "tbl_bookmarks",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "document_id"])
    ]
)
class Bookmark(
    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    var document: Document,
) : AbstractEntity<UUID>()
