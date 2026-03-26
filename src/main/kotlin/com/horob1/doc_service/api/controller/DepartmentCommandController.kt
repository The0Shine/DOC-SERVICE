package com.horob1.doc_service.api.controller

import com.horob1.doc_service.api.dto.request.CreateUpdateDepartmentDto
import com.horob1.doc_service.api.dto.response.DepartmentResponse
import com.horob1.doc_service.service.command.DepartmentCommandHandler
import com.horob1.doc_service.shared.dto.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/departments")
class DepartmentCommandController(
    private val departmentCommandHandler: DepartmentCommandHandler
) {
    @PostMapping
    fun create(
        @Valid @RequestBody dto: CreateUpdateDepartmentDto
    ): ResponseEntity<ApiResponse<DepartmentResponse>> {
        val apiResponse = ApiResponse.success(
            message = "Create department successfully",
            status = HttpStatus.CREATED,
            data = departmentCommandHandler.create(dto)
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    @PutMapping("/{departmentId}")
    fun update(
        @PathVariable departmentId: String,
        @Valid @RequestBody dto: CreateUpdateDepartmentDto
    ): ResponseEntity<ApiResponse<DepartmentResponse>> {
        val apiResponse = ApiResponse.success(
            message = "Update department successfully",
            status = HttpStatus.OK,
            data = departmentCommandHandler.update(UUID.fromString(departmentId), dto)
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }

    @DeleteMapping("/{departmentId}")
    fun delete(
        @PathVariable departmentId: String
    ): ResponseEntity<ApiResponse<Nothing>> {
        departmentCommandHandler.delete(UUID.fromString(departmentId))
        val apiResponse = ApiResponse.success<Nothing>(
            message = "Delete department successfully",
            status = HttpStatus.OK,
        )
        return ResponseEntity.status(apiResponse.status).body(apiResponse)
    }
}
