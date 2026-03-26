package com.horob1.doc_service.infrastructure.repository.postgres

import com.horob1.doc_service.domain.model.school.School
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SchoolPostgresRepository : JpaRepository<School, UUID> {
    fun findByName(name: String): School?
    fun existsByName(name: String): Boolean
}
