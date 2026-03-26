package com.horob1.doc_service.service.command

import com.horob1.doc_service.api.dto.request.CreateUpdateSchoolDto
import com.horob1.doc_service.api.dto.response.SchoolResponse
import com.horob1.doc_service.api.dto.response.toResponse
import com.horob1.doc_service.api.exception.SchoolAlreadyExists
import com.horob1.doc_service.api.exception.SchoolNotFound
import com.horob1.doc_service.domain.model.school.School
import com.horob1.doc_service.domain.repository.SchoolRepository
import com.horob1.doc_service.shared.exception.AppException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SchoolCommandHandler(
    private val schoolRepository: SchoolRepository
) {
    fun create(dto: CreateUpdateSchoolDto): SchoolResponse {
        if (schoolRepository.existsByName(dto.name)) {
            throw AppException(SchoolAlreadyExists)
        }

        return schoolRepository.save(
            School(
                name = dto.name,
                description = dto.description,
                address = dto.address,
            )
        ).toResponse()
    }

    fun update(id: UUID, dto: CreateUpdateSchoolDto): SchoolResponse {
        val school = schoolRepository.findById(id) ?: throw AppException(SchoolNotFound)

        val existingByName = schoolRepository.findByName(dto.name)
        if (existingByName != null && existingByName.id != id) {
            throw AppException(SchoolAlreadyExists)
        }

        school.name = dto.name
        school.description = dto.description
        school.address = dto.address

        return schoolRepository.save(school).toResponse()
    }

    fun delete(id: UUID) {
        val school = schoolRepository.findById(id) ?: throw AppException(SchoolNotFound)
        schoolRepository.delete(school)
    }
}
