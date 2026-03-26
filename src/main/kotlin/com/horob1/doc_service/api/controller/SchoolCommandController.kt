package com.horob1.doc_service.api.controller

import com.horob1.doc_service.api.dto.request.CreateUpdateSchoolDto
import com.horob1.doc_service.api.dto.response.SchoolResponse
import com.horob1.doc_service.service.command.SchoolCommandHandler
import com.horob1.doc_service.shared.dto.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/schools")
class SchoolCommandController(
    private val schoolCommandHandler: SchoolCommandHandler
) {
    @PostMapping
    fun create(
        @Valid @RequestBody dto: CreateUpdateSchoolDto
    ): ResponseEntity<ApiResponse<SchoolResponse>> {
        val apiResponse = ApiResponse.success(
            message = "Create school successfully",
            status = HttpStatus.CREATED,
            data = schoolCommandHandler.create(dto)
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    @PutMapping("/{schoolId}")
    fun update(
        @PathVariable schoolId: String,
        @Valid @RequestBody dto: CreateUpdateSchoolDto
    ): ResponseEntity<ApiResponse<SchoolResponse>> {
        val apiResponse = ApiResponse.success(
            message = "Update school successfully",
            status = HttpStatus.OK,
            data = schoolCommandHandler.update(java.util.UUID.fromString(schoolId), dto)
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    @DeleteMapping("/{schoolId}")
    fun delete(
        @PathVariable schoolId: String
    ): ResponseEntity<ApiResponse<Nothing>> {
        schoolCommandHandler.delete(java.util.UUID.fromString(schoolId))
        val apiResponse = ApiResponse.success<Nothing>(
            message = "Delete school successfully",
            status = HttpStatus.OK,
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }
}
