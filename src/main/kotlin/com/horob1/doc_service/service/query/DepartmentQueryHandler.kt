package com.horob1.doc_service.service.query

import com.horob1.doc_service.api.dto.response.DepartmentResponse
import com.horob1.doc_service.api.dto.response.toResponse
import com.horob1.doc_service.api.exception.DepartmentNotFound
import com.horob1.doc_service.domain.repository.DepartmentRepository
import com.horob1.doc_service.shared.exception.AppException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DepartmentQueryHandler(
    private val departmentRepository: DepartmentRepository
) {
    fun getAll(): List<DepartmentResponse> {
        return departmentRepository.findAll().map { it.toResponse() }
    }

    fun getById(id: UUID): DepartmentResponse {
        val department = departmentRepository.findById(id) ?: throw AppException(DepartmentNotFound)
        return department.toResponse()
    }

    fun getBySchoolId(schoolId: UUID): List<DepartmentResponse> {
        return departmentRepository.findBySchoolId(schoolId).map { it.toResponse() }
    }
}
