package com.horob1.doc_service.infrastructure.repository

import com.horob1.doc_service.domain.model.school.School
import com.horob1.doc_service.domain.repository.SchoolRepository
import com.horob1.doc_service.infrastructure.repository.postgres.SchoolPostgresRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class SchoolRepositoryImpl(
    private val schoolPostgresRepository: SchoolPostgresRepository
) : SchoolRepository {

    override fun findById(id: UUID): School? {
        return schoolPostgresRepository.findById(id).orElse(null)
    }

    override fun findByName(name: String): School? {
        return schoolPostgresRepository.findByName(name)
    }

    override fun findAll(): List<School> {
        return schoolPostgresRepository.findAll()
    }

    override fun save(school: School): School {
        return schoolPostgresRepository.save(school)
    }

    override fun delete(school: School) {
        schoolPostgresRepository.delete(school)
    }

    override fun existsByName(name: String): Boolean {
        return schoolPostgresRepository.existsByName(name)
    }
}
