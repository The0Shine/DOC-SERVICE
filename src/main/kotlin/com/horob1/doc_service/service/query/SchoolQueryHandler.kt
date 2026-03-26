package com.horob1.doc_service.service.query

import com.horob1.doc_service.api.dto.response.SchoolResponse
import com.horob1.doc_service.api.dto.response.toResponse
import com.horob1.doc_service.api.exception.SchoolNotFound
import com.horob1.doc_service.domain.repository.SchoolRepository
import com.horob1.doc_service.shared.exception.AppException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SchoolQueryHandler(
    private val schoolRepository: SchoolRepository
) {
    fun getAll(): List<SchoolResponse> {
        return schoolRepository.findAll().map { it.toResponse() }
    }

    fun getById(id: UUID): SchoolResponse {
        val school = schoolRepository.findById(id) ?: throw AppException(SchoolNotFound)
        return school.toResponse()
    }
}
