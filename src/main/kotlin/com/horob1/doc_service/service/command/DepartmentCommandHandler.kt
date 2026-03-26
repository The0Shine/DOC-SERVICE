package com.horob1.doc_service.service.command

import com.horob1.doc_service.api.dto.request.CreateUpdateDepartmentDto
import com.horob1.doc_service.api.dto.response.DepartmentResponse
import com.horob1.doc_service.api.dto.response.toResponse
import com.horob1.doc_service.api.exception.DepartmentAlreadyExists
import com.horob1.doc_service.api.exception.DepartmentNotFound
import com.horob1.doc_service.api.exception.SchoolNotFound
import com.horob1.doc_service.domain.model.department.Department
import com.horob1.doc_service.domain.repository.DepartmentRepository
import com.horob1.doc_service.domain.repository.SchoolRepository
import com.horob1.doc_service.shared.exception.AppException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DepartmentCommandHandler(
    private val departmentRepository: DepartmentRepository,
    private val schoolRepository: SchoolRepository
) {
    fun create(dto: CreateUpdateDepartmentDto): DepartmentResponse {
        val school = schoolRepository.findById(dto.schoolId) ?: throw AppException(SchoolNotFound)

        if (departmentRepository.existsByNameAndSchoolId(dto.name, dto.schoolId)) {
            throw AppException(DepartmentAlreadyExists)
        }

        return departmentRepository.save(
            Department(
                name = dto.name,
                description = dto.description,
                school = school,
            )
        ).toResponse()
    }

    fun update(id: UUID, dto: CreateUpdateDepartmentDto): DepartmentResponse {
        val department = departmentRepository.findById(id) ?: throw AppException(DepartmentNotFound)
        val school = schoolRepository.findById(dto.schoolId) ?: throw AppException(SchoolNotFound)

        if (departmentRepository.existsByNameAndSchoolId(dto.name, dto.schoolId) && department.name != dto.name) {
            throw AppException(DepartmentAlreadyExists)
        }

        department.name = dto.name
        department.description = dto.description
        department.school = school

        return departmentRepository.save(department).toResponse()
    }

    fun delete(id: UUID) {
        val department = departmentRepository.findById(id) ?: throw AppException(DepartmentNotFound)
        departmentRepository.delete(department)
    }
}
