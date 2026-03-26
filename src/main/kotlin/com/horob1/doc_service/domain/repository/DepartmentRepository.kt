package com.horob1.doc_service.domain.repository

import com.horob1.doc_service.domain.model.department.Department
import java.util.UUID

interface DepartmentRepository {
    fun findById(id: UUID): Department?
    fun findAll(): List<Department>
    fun findBySchoolId(schoolId: UUID): List<Department>
    fun save(department: Department): Department
    fun delete(department: Department)
    fun existsByNameAndSchoolId(name: String, schoolId: UUID): Boolean
}
