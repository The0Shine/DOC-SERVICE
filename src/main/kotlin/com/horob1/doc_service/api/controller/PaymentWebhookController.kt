package com.horob1.doc_service.api.controller

import com.horob1.doc_service.api.dto.request.SepayWebhookDto
import com.horob1.doc_service.config.SepayConfig
import com.horob1.doc_service.service.command.PurchaseCommandHandler
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/transactions")
class PaymentWebhookController(
    private val purchaseCommandHandler: PurchaseCommandHandler,
    private val sepayConfig: SepayConfig,
) {
    private val logger = LoggerFactory.getLogger(PaymentWebhookController::class.java)

    @PostMapping("/payment-confirmation")
    fun handleSepayWebhook(
        @RequestHeader("Authorization") authorization: String?,
        @RequestBody dto: SepayWebhookDto,
    ): ResponseEntity<Map<String, Boolean>> {
        // Verify API Key
        val expectedHeader = "Apikey ${sepayConfig.apiKey}"
        if (authorization != expectedHeader) {
            logger.warn("[SEPAY] Invalid API key received")
            return ResponseEntity.status(401).body(mapOf("success" to false))
        }

        // Only process incoming transfers
        if (dto.transferType != "in") {
            return ResponseEntity.ok(mapOf("success" to true))
        }

        val success = purchaseCommandHandler.confirmPayment(dto)
        return if (success) {
            ResponseEntity.ok(mapOf("success" to true))
        } else {
            ResponseEntity.status(400).body(mapOf("success" to false))
        }
    }
}
