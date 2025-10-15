package com.employeemgmt.employeeservice.controller;

import com.employeemgmt.employeeservice.dto.DepartmentRequest;
import com.employeemgmt.employeeservice.dto.DepartmentResponse;
import com.employeemgmt.employeeservice.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        DepartmentResponse response = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }
}