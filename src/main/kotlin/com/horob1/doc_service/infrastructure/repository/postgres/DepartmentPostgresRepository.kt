package com.horob1.doc_service.infrastructure.repository.postgres

import com.horob1.doc_service.domain.model.department.Department
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DepartmentPostgresRepository : JpaRepository<Department, UUID> {
    fun findBySchoolId(schoolId: UUID): List<Department>
    fun existsByNameAndSchoolId(name: String, schoolId: UUID): Boolean
}
