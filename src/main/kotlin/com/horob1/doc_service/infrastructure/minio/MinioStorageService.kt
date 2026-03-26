package com.horob1.doc_service.infrastructure.minio

import io.minio.*
import io.minio.http.Method
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream
import java.util.concurrent.TimeUnit

@Service
class MinioStorageService(
    private val minioClient: MinioClient,
    @Value("\${minio.bucket}") private val defaultBucket: String
) {
    private val logger = LoggerFactory.getLogger(MinioStorageService::class.java)

    fun ensureBucketExists(bucket: String = defaultBucket) {
        val exists = minioClient.bucketExists(
            BucketExistsArgs.builder().bucket(bucket).build()
        )
        if (!exists) {
            minioClient.makeBucket(
                MakeBucketArgs.builder().bucket(bucket).build()
            )
            logger.info("Created MinIO bucket: {}", bucket)
        }
    }

    fun uploadFile(
        key: String,
        inputStream: InputStream,
        size: Long,
        contentType: String,
        bucket: String = defaultBucket
    ) {
        ensureBucketExists(bucket)
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucket)
                .`object`(key)
                .stream(inputStream, size, -1)
                .contentType(contentType)
                .build()
        )
        logger.info("Uploaded file to MinIO: bucket={}, key={}", bucket, key)
    }

    fun getPresignedUrl(
        key: String,
        bucket: String = defaultBucket,
        expiryMinutes: Int = 15
    ): String {
        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .bucket(bucket)
                .`object`(key)
                .method(Method.GET)
                .expiry(expiryMinutes, TimeUnit.MINUTES)
                .build()
        )
    }

    fun deleteFile(key: String, bucket: String = defaultBucket) {
        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucket)
                .`object`(key)
                .build()
        )
        logger.info("Deleted file from MinIO: bucket={}, key={}", bucket, key)
    }

    fun getDefaultBucket(): String = defaultBucket
}
