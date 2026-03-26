package com.horob1.doc_service.api.dto.response

import com.horob1.doc_service.domain.model.school.School
import java.time.Instant
import java.util.UUID

data class SchoolResponse(
    val id: UUID,
    val name: String,
    val description: String,
    val address: String,
    val createdAt: Instant?,
    val updatedAt: Instant?,
)

fun School.toResponse() = SchoolResponse(
    id = this.id!!,
    name = this.name,
    description = this.description,
    address = this.address,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)
