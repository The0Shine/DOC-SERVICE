package com.horob1.doc_service.api.dto.response

import com.horob1.doc_service.domain.model.department.Department
import java.time.Instant
import java.util.UUID

data class DepartmentResponse(
    val id: UUID,
    val name: String,
    val description: String,
    val schoolId: UUID,
    val schoolName: String,
    val createdAt: Instant?,
    val updatedAt: Instant?,
)

fun Department.toResponse() = DepartmentResponse(
    id = this.id!!,
    name = this.name,
    description = this.description,
    schoolId = this.school.id!!,
    schoolName = this.school.name,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)
