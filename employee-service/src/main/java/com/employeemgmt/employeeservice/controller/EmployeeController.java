package com.employeemgmt.employeeservice.controller;

import com.employeemgmt.employeeservice.dto.EmployeeRequest;
import com.employeemgmt.employeeservice.dto.EmployeeResponse;
import com.employeemgmt.employeeservice.entity.EmployeeStatus;
import com.employeemgmt.employeeservice.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/employees")
@Tag(name = "Employee Management", description = "APIs for managing employees")
@SecurityRequirement(name = "Bearer Authentication")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

@Operation(summary = "Create a new employee", description = "Only ADMIN can create employees")
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Employee created"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
})
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        // Role-based access control: Only ADMIN can create employees
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        EmployeeResponse response = employeeService.createEmployee(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



@Operation(summary = "Update an employee", description = "Only ADMIN can update employees")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
})
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        // Role-based access control: Only ADMIN can update employees
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        EmployeeResponse response = employeeService.updateEmployee(id, request, userId);
        return ResponseEntity.ok(response);
    }

@Operation(summary = "Delete employee by id", description = "Only ADMIN can delete employees")
@ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Employee deleted"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN allowed")
})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        // Role-based access control: Only ADMIN can delete employees
        if (!"ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        employeeService.deleteEmployee(id, userId);
        return ResponseEntity.noContent().build();
    }

@Operation(summary = "Get employee by id", description = "ADMIN/MANAGER can view any, EMPLOYEE can view self")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Employee retrieved"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
})
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        EmployeeResponse response = employeeService.getEmployeeById(id);

        // Role-based access control
        // EMPLOYEE can only view their own details
        if ("EMPLOYEE".equals(userRole) && !userId.equals(response.getEmployeeId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get employee code by id", description = "Only Emplyee can view employees")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee gotten successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not allowed"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })

    @GetMapping("/code/{employeeId}")
    public ResponseEntity<EmployeeResponse> getEmployeeByEmployeeId(
            @PathVariable String employeeId,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        EmployeeResponse response = employeeService.getEmployeeByEmployeeId(employeeId);

        // EMPLOYEE can only view their own details
        if ("EMPLOYEE".equals(userRole) && !userId.equals(employeeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "List all employees", description = "ADMIN and MANAGER only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employees listed"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        // Only ADMIN and MANAGER can view all employees
        if ("EMPLOYEE".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "List employees by department", description = "ADMIN and MANAGER only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employees listed"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByDepartment(
            @PathVariable Long departmentId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        // MANAGER can view employees in their department
        // ADMIN can view all
        if ("EMPLOYEE".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<EmployeeResponse> employees = employeeService.getEmployeesByDepartment(departmentId);
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "List employees by status", description = "ADMIN and MANAGER only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employees listed"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByStatus(
            @PathVariable EmployeeStatus status,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        if ("EMPLOYEE".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<EmployeeResponse> employees = employeeService.getEmployeesByStatus(status);
        return ResponseEntity.ok(employees);
    }



    @Operation(summary = "Search employees by name", description = "ADMIN and MANAGER only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employees found"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeResponse>> searchEmployees(
            @RequestParam String name,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        if ("EMPLOYEE".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<EmployeeResponse> employees = employeeService.searchEmployeesByName(name);
        return ResponseEntity.ok(employees);
    }
}
