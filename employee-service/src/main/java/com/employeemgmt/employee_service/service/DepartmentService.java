package com.employeemgmt.employeeservice.service;

import com.employeemgmt.employeeservice.dto.DepartmentRequest;
import com.employeemgmt.employeeservice.dto.DepartmentResponse;
import com.employeemgmt.employeeservice.entity.Department;
import com.employeemgmt.employeeservice.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new RuntimeException("Department already exists: " + request.getName());
        }

        Department department = new Department();
        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setManagerName(request.getManagerName());

        department = departmentRepository.save(department);
        return mapToResponse(department);
    }

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        // Check if name changed and already exists
        if (!department.getName().equals(request.getName()) &&
                departmentRepository.existsByName(request.getName())) {
            throw new RuntimeException("Department name already exists: " + request.getName());
        }

        department.setName(request.getName());
        department.setDescription(request.getDescription());
        department.setManagerName(request.getManagerName());

        department = departmentRepository.save(department);
        return mapToResponse(department);
    }

    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        if (department.getEmployees() != null && !department.getEmployees().isEmpty()) {
            throw new RuntimeException("Cannot delete department with existing employees");
        }

        departmentRepository.delete(department);
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        return mapToResponse(department);
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private DepartmentResponse mapToResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .managerName(department.getManagerName())
                .employeeCount(department.getEmployees() != null ? department.getEmployees().size() : 0)
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }
}