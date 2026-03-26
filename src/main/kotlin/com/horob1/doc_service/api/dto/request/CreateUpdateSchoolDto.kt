package com.horob1.doc_service.api.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateUpdateSchoolDto(
    @field:NotBlank(message = "School name is required")
    @field:Size(max = 255, message = "School name must be at most 255 characters")
    val name: String,

    @field:Size(max = 1000, message = "Description must be at most 1000 characters")
    val description: String = "",

    @field:Size(max = 500, message = "Address must be at most 500 characters")
    val address: String = "",
)
