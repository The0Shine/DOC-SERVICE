package com.horob1.doc_service.domain.model.document

import com.horob1.doc_service.domain.model.AbstractEntity
import com.horob1.doc_service.domain.model.department.Department
import com.horob1.doc_service.domain.model.school.School
import com.horob1.doc_service.shared.enums.DocumentStatus
import com.horob1.doc_service.shared.enums.FileType
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(
    name = "tbl_documents",
    indexes = [
        Index(name = "idx_doc_owner", columnList = "owner_id"),
        Index(name = "idx_doc_status", columnList = "status"),
        Index(name = "idx_doc_school", columnList = "school_id"),
        Index(name = "idx_doc_department", columnList = "department_id")
    ]
)
class Document(
    @Column(nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var description: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var fileType: FileType,

    @Column(nullable = false)
    var minioKey: String,

    @Column(nullable = false)
    var minioBucket: String,

    @Column(nullable = false)
    var originalFileName: String,

    @Column(nullable = false)
    var fileSize: Long,

    @Column(nullable = false)
    var docHash: String,

    @Column(nullable = false)
    var hashAlgo: String = "SHA256",

    @Column
    var blockchainDocId: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: DocumentStatus = DocumentStatus.ACTIVE,

    @Column(nullable = false)
    var price: BigDecimal = BigDecimal.ZERO,

    @Column(name = "owner_id", nullable = false)
    var ownerId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id")
    var school: School? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    var department: Department? = null,
) : AbstractEntity<UUID>()
