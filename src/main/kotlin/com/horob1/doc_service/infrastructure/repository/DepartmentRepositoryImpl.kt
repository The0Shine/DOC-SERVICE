package com.horob1.doc_service.infrastructure.repository

import com.horob1.doc_service.domain.model.department.Department
import com.horob1.doc_service.domain.repository.DepartmentRepository
import com.horob1.doc_service.infrastructure.repository.postgres.DepartmentPostgresRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class DepartmentRepositoryImpl(
    private val departmentPostgresRepository: DepartmentPostgresRepository
) : DepartmentRepository {

    override fun findById(id: UUID): Department? {
        return departmentPostgresRepository.findById(id).orElse(null)
    }

    override fun findAll(): List<Department> {
        return departmentPostgresRepository.findAll()
    }

    override fun findBySchoolId(schoolId: UUID): List<Department> {
        return departmentPostgresRepository.findBySchoolId(schoolId)
    }

    override fun save(department: Department): Department {
        return departmentPostgresRepository.save(department)
    }

    override fun delete(department: Department) {
        departmentPostgresRepository.delete(department)
    }

    override fun existsByNameAndSchoolId(name: String, schoolId: UUID): Boolean {
        return departmentPostgresRepository.existsByNameAndSchoolId(name, schoolId)
    }
}
