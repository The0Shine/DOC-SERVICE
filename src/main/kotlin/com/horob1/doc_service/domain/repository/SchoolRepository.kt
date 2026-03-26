package com.horob1.doc_service.domain.repository

import com.horob1.doc_service.domain.model.school.School
import java.util.UUID

interface SchoolRepository {
    fun findById(id: UUID): School?
    fun findByName(name: String): School?
    fun findAll(): List<School>
    fun save(school: School): School
    fun delete(school: School)
    fun existsByName(name: String): Boolean
}
