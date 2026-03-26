package com.horob1.doc_service.service.command

import com.horob1.doc_service.api.dto.request.CreateDocumentDto
import com.horob1.doc_service.api.dto.request.UpdateDocumentDto
import com.horob1.doc_service.api.dto.response.DocumentResponse
import com.horob1.doc_service.api.exception.*
import com.horob1.doc_service.domain.model.document.Document
import com.horob1.doc_service.domain.model.document.toResponse
import com.horob1.doc_service.domain.repository.DepartmentRepository
import com.horob1.doc_service.domain.repository.DocumentRepository
import com.horob1.doc_service.domain.repository.SchoolRepository
import com.horob1.doc_service.infrastructure.minio.MinioStorageService
import com.horob1.doc_service.infrastructure.producer.DocEventProducer
import com.horob1.doc_service.shared.enums.DocumentStatus
import com.horob1.doc_service.shared.enums.FileType
import com.horob1.doc_service.shared.exception.AppException
import com.horob1.doc_service.shared.kafka.event.DocumentCreateEvent
import com.horob1.doc_service.shared.kafka.event.DocumentDeleteEvent
import com.horob1.doc_service.util.FileHashUtil
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class DocumentCommandHandler(
    private val documentRepository: DocumentRepository,
    private val schoolRepository: SchoolRepository,
    private val departmentRepository: DepartmentRepository,
    private val minioStorageService: MinioStorageService,
    private val docEventProducer: DocEventProducer,
) {
    private val logger = LoggerFactory.getLogger(DocumentCommandHandler::class.java)

    companion object {
        private val ALLOWED_CONTENT_TYPES = mapOf(
            "application/pdf" to FileType.PDF,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" to FileType.WORD,
            "application/msword" to FileType.WORD,
            "text/plain" to FileType.TEXT,
        )

        private val ALLOWED_EXTENSIONS = mapOf(
            "pdf" to FileType.PDF,
            "docx" to FileType.WORD,
            "doc" to FileType.WORD,
            "txt" to FileType.TEXT,
        )
    }

    fun create(file: MultipartFile, dto: CreateDocumentDto, ownerId: UUID): DocumentResponse {
        // 1. Validate file type
        val fileType = resolveFileType(file)

        // 2. Resolve school & department
        val school = dto.schoolId?.let {
            schoolRepository.findById(it) ?: throw AppException(SchoolNotFound)
        }
        val department = dto.departmentId?.let {
            departmentRepository.findById(it) ?: throw AppException(DepartmentNotFound)
        }

        // 3. Stream file → hash + upload MinIO simultaneously
        val originalFileName = file.originalFilename ?: "unknown"
        val minioKey = "documents/$ownerId/${UUID.randomUUID()}_$originalFileName"
        val bucket = minioStorageService.getDefaultBucket()

        val (digestInputStream, getHash) = FileHashUtil.sha256(file.inputStream)

        try {
            // Upload to MinIO using DigestInputStream (hash computed during stream)
            minioStorageService.uploadFile(
                key = minioKey,
                inputStream = digestInputStream,
                size = file.size,
                contentType = file.contentType ?: "application/octet-stream",
            )
        } catch (e: Exception) {
            logger.error("Failed to upload file to MinIO: {}", e.message, e)
            throw AppException(FileUploadFailed)
        }

        // 4. Get computed hash
        val docHash = getHash()

        // 5. Save metadata to PostgreSQL
        val document = documentRepository.save(
            Document(
                title = dto.title,
                description = dto.description,
                fileType = fileType,
                minioKey = minioKey,
                minioBucket = bucket,
                originalFileName = originalFileName,
                fileSize = file.size,
                docHash = docHash,
                hashAlgo = "SHA256",
                price = dto.price,
                ownerId = ownerId,
                school = school,
                department = department,
            )
        )

        // 6. Publish Kafka event for blockchain registration (async)
        try {
            docEventProducer.sendDocumentCreateEvent(
                DocumentCreateEvent(
                    documentId = document.id.toString(),
                    docHash = docHash,
                    hashAlgo = "SHA256",
                    systemUserId = ownerId.toString(),
                )
            )
        } catch (e: Exception) {
            logger.warn("Failed to send blockchain event for document {}: {}", document.id, e.message)
        }

        return document.toResponse()
    }

    fun update(id: UUID, dto: UpdateDocumentDto, userId: UUID): DocumentResponse {
        val document = documentRepository.findById(id) ?: throw AppException(DocumentNotFound)

        if (document.status == DocumentStatus.DELETED) {
            throw AppException(DocumentAlreadyDeleted)
        }

        if (document.ownerId != userId) {
            throw AppException(NotDocumentOwner)
        }

        val school = dto.schoolId?.let {
            schoolRepository.findById(it) ?: throw AppException(SchoolNotFound)
        }
        val department = dto.departmentId?.let {
            departmentRepository.findById(it) ?: throw AppException(DepartmentNotFound)
        }

        document.title = dto.title
        document.description = dto.description
        document.price = dto.price
        document.school = school
        document.department = department

        return documentRepository.save(document).toResponse()
    }

    fun delete(id: UUID, userId: UUID) {
        val document = documentRepository.findById(id) ?: throw AppException(DocumentNotFound)

        if (document.status == DocumentStatus.DELETED) {
            throw AppException(DocumentAlreadyDeleted)
        }

        if (document.ownerId != userId) {
            throw AppException(NotDocumentOwner)
        }

        document.status = DocumentStatus.DELETED
        documentRepository.save(document)

        try {
            docEventProducer.sendDocumentDeleteEvent(
                DocumentDeleteEvent(documentId = id.toString())
            )
        } catch (e: Exception) {
            logger.warn("[KAFKA] Failed to send document.delete for {}: {}", id, e.message)
        }
    }

    private fun resolveFileType(file: MultipartFile): FileType {
        // Try content type first
        val contentType = file.contentType
        if (contentType != null && ALLOWED_CONTENT_TYPES.containsKey(contentType)) {
            return ALLOWED_CONTENT_TYPES[contentType]!!
        }

        // Fallback to extension
        val extension = file.originalFilename?.substringAfterLast(".", "")?.lowercase()
        if (extension != null && ALLOWED_EXTENSIONS.containsKey(extension)) {
            return ALLOWED_EXTENSIONS[extension]!!
        }

        throw AppException(InvalidFileType)
    }
}
