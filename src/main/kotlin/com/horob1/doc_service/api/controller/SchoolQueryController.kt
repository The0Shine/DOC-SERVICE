package com.horob1.doc_service.api.controller

import com.horob1.doc_service.api.dto.response.DepartmentResponse
import com.horob1.doc_service.api.dto.response.SchoolResponse
import com.horob1.doc_service.service.query.DepartmentQueryHandler
import com.horob1.doc_service.service.query.SchoolQueryHandler
import com.horob1.doc_service.shared.dto.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/schools")
class SchoolQueryController(
    private val schoolQueryHandler: SchoolQueryHandler,
    private val departmentQueryHandler: DepartmentQueryHandler
) {
    @GetMapping
    fun getAll(): ResponseEntity<ApiResponse<List<SchoolResponse>>> {
        val apiResponse = ApiResponse.success(
            message = "Get all schools successfully",
            status = HttpStatus.OK,
            data = schoolQueryHandler.getAll()
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    @GetMapping("/{schoolId}")
    fun getById(
        @PathVariable schoolId: String
    ): ResponseEntity<ApiResponse<SchoolResponse>> {
        val apiResponse = ApiResponse.success(
            message = "Get school successfully",
            status = HttpStatus.OK,
            data = schoolQueryHandler.getById(UUID.fromString(schoolId))
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    @GetMapping("/{schoolId}/departments")
    fun getDepartmentsBySchool(
        @PathVariable schoolId: String
    ): ResponseEntity<ApiResponse<List<DepartmentResponse>>> {
        val apiResponse = ApiResponse.success(
            message = "Get departments by school successfully",
            status = HttpStatus.OK,
            data = departmentQueryHandler.getBySchoolId(UUID.fromString(schoolId))
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }
}
