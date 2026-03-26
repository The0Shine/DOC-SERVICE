package com.horob1.doc_service.domain.model.department

import com.horob1.doc_service.domain.model.AbstractEntity
import com.horob1.doc_service.domain.model.school.School
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
    name = "tbl_departments",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["name", "school_id"])
    ]
)
class Department(
    @Column(nullable = false)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    var school: School,
) : AbstractEntity<UUID>()
