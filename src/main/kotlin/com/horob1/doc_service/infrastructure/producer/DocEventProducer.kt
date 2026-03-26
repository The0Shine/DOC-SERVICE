package com.horob1.doc_service.infrastructure.producer

import com.horob1.doc_service.shared.kafka.event.AccessGrantEvent
import com.horob1.doc_service.shared.kafka.event.AccessRevokeEvent
import com.horob1.doc_service.shared.kafka.event.DocumentCreateEvent
import com.horob1.doc_service.shared.kafka.event.DocumentDeleteEvent
import com.horob1.doc_service.shared.kafka.topic.DocTopic
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class DocEventProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    private val logger = LoggerFactory.getLogger(DocEventProducer::class.java)

    fun sendDocumentCreateEvent(event: DocumentCreateEvent) {
        send(DocTopic.DOCUMENT_CREATE, event.documentId, event, "document.create")
    }

    fun sendDocumentDeleteEvent(event: DocumentDeleteEvent) {
        send(DocTopic.DOCUMENT_DELETE, event.documentId, event, "document.delete")
    }

    fun sendAccessGrantEvent(event: AccessGrantEvent) {
        send(DocTopic.ACCESS_GRANT, event.documentId, event, "access.grant")
    }

    fun sendAccessRevokeEvent(event: AccessRevokeEvent) {
        send(DocTopic.ACCESS_REVOKE, event.documentId, event, "access.revoke")
    }

    private fun send(topic: String, key: String, payload: Any, label: String) {
        kafkaTemplate.send(topic, key, payload)
            .whenComplete { result, ex ->
                if (ex != null) {
                    logger.error("[KAFKA] Failed to send {}: key={} error={}", label, key, ex.message, ex)
                } else {
                    logger.info("[KAFKA] Sent {}: key={} offset={}", label, key, result.recordMetadata.offset())
                }
            }
    }
}
