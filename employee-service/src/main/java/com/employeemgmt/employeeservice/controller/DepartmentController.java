package com.employeemgmt.employeeservice.controller;

import com.employeemgmt.employeeservice.dto.DepartmentRequest;
import com.employeemgmt.employeeservice.dto.DepartmentResponse;
import com.employeemgmt.employeeservice.service.DepartmentService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/departments")
@Tag(name = "Department Management")
@SecurityRequirement(name = "Bearer Authentication")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @Operation(summary = "Create department", description = "ADMIN only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Department created"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(
            @Valid @RequestBody DepartmentRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        // Only ADMIN can create departments
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        DepartmentResponse response = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update department", description = "ADMIN only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Department updated"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        // Only ADMIN can update departments
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        DepartmentResponse response = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete department", description = "ADMIN only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Department deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        // Only ADMIN can delete departments
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get department by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Department retrieved")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        DepartmentResponse response = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List all departments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departments listed")
    })
    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }
}
