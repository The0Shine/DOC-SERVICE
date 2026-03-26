package com.horob1.doc_service.api.dto.request

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class SepayWebhookDto(
    val id: Long,
    val gateway: String? = null,
    val transactionDate: String? = null,
    val accountNumber: String? = null,
    val code: String? = null,
    val content: String? = null,
    val transferType: String? = null,
    val transferAmount: BigDecimal,
    val accumulated: BigDecimal? = null,
    val subAccount: String? = null,
    val referenceCode: String? = null,
    val description: String? = null,
)
