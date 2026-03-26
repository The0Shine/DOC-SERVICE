package com.horob1.doc_service.api.controller

import com.horob1.doc_service.api.dto.response.DepartmentResponse
import com.horob1.doc_service.service.query.DepartmentQueryHandler
import com.horob1.doc_service.shared.dto.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/departments")
class DepartmentQueryController(
    private val departmentQueryHandler: DepartmentQueryHandler
) {
    @GetMapping
    fun getAll(): ResponseEntity<ApiResponse<List<DepartmentResponse>>> {
        val apiResponse = ApiResponse.success(
            message = "Get all departments successfully",
            status = HttpStatus.OK,
            data = departmentQueryHandler.getAll()
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    @GetMapping("/{departmentId}")
    fun getById(
        @PathVariable departmentId: String
    ): ResponseEntity<ApiResponse<DepartmentResponse>> {
        val apiResponse = ApiResponse.success(
            message = "Get department successfully",
            status = HttpStatus.OK,
            data = departmentQueryHandler.getById(UUID.fromString(departmentId))
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }
}
