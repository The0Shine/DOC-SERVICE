package com.horob1.doc_service.api.dto.request

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.UUID

data class UpdateDocumentDto(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 500, message = "Title must be at most 500 characters")
    val title: String,

    @field:Size(max = 2000, message = "Description must be at most 2000 characters")
    val description: String = "",

    @field:DecimalMin(value = "0", message = "Price must not be negative")
    val price: BigDecimal = BigDecimal.ZERO,

    val schoolId: UUID? = null,

    val departmentId: UUID? = null,
)
