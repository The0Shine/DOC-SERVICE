package com.horob1.doc_service.api.dto.request

import jakarta.validation.constraints.NotNull
import java.util.UUID

data class CreatePurchaseDto(
    @field:NotNull(message = "Document ID is required")
    val documentId: UUID,
)
