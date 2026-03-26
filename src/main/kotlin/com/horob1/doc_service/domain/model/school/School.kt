package com.horob1.doc_service.domain.model.school

import com.horob1.doc_service.domain.model.AbstractEntity
import com.horob1.doc_service.domain.model.department.Department
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(
    name = "tbl_schools",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["name"])
    ]
)
class School(
    @Column(nullable = false, unique = true)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var description: String = "",

    @Column
    var address: String = "",

    @OneToMany(mappedBy = "school", fetch = FetchType.LAZY)
    var departments: MutableList<Department> = mutableListOf(),
) : AbstractEntity<UUID>()
