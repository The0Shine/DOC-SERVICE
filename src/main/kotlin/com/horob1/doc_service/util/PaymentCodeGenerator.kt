package com.horob1.doc_service.util

import java.util.UUID

object PaymentCodeGenerator {
    fun generate(): String {
        val shortId = UUID.randomUUID().toString().replace("-", "").substring(0, 8).uppercase()
        return "DOCUBE$shortId"
    }
}
