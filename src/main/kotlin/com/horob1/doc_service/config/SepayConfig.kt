package com.horob1.doc_service.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.math.BigDecimal

@Configuration
class SepayConfig(
    @Value("\${sepay.api-key}") val apiKey: String,
    @Value("\${sepay.bank-account}") val bankAccount: String,
    @Value("\${sepay.bank-code}") val bankCode: String,
    @Value("\${sepay.qr-base-url}") val qrBaseUrl: String,
) {
    fun buildQrUrl(amount: BigDecimal, paymentCode: String): String {
        return "${qrBaseUrl}?acc=${bankAccount}&bank=${bankCode}&amount=${amount.toLong()}&des=${paymentCode}"
    }
}
