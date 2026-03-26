package com.horob1.doc_service.shared.kafka.event

data class DocumentCreateEvent(
    val documentId: String,
    val docHash: String,
    val hashAlgo: String,
    val systemUserId: String,
)

data class DocumentDeleteEvent(
    val documentId: String,
)

data class AccessGrantEvent(
    val documentId: String,
    val granteeUserId: String,
    val granteeUserMsp: String = "AdminOrgMSP",
    val systemUserId: String,
)

data class AccessRevokeEvent(
    val documentId: String,
    val userId: String,
)
