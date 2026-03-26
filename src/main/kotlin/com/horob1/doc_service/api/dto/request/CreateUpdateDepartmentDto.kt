package com.horob1.doc_service.api.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.UUID

data class CreateUpdateDepartmentDto(
    @field:NotBlank(message = "Department name is required")
    @field:Size(max = 255, message = "Department name must be at most 255 characters")
    val name: String,

    @field:Size(max = 1000, message = "Description must be at most 1000 characters")
    val description: String = "",

    @field:NotNull(message = "School ID is required")
    val schoolId: UUID,
)
