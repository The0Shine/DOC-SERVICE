package com.horob1.doc_service.domain.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import java.io.Serializable
import java.time.Instant

@MappedSuperclass()
abstract class AbstractEntity<T : Serializable> : Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    var id: T? = null

    @CreatedBy
    @Column(name = "created_by")
    var createdBy: T? = null

    @LastModifiedBy
    @Column(name = "updated_by")
    var updatedBy: T? = null

    @Column(name = "created_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    var createdAt: Instant? = null

    @Column(name = "updated_at")
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    var updatedAt: Instant? = null
}
