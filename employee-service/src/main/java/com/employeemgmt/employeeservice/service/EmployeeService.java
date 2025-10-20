package com.employeemgmt.employeeservice.service;

import com.employeemgmt.employeeservice.dto.EmployeeRequest;
import com.employeemgmt.employeeservice.dto.EmployeeResponse;
import com.employeemgmt.employeeservice.entity.Department;
import com.employeemgmt.employeeservice.entity.Employee;
import com.employeemgmt.employeeservice.entity.EmployeeStatus;
import com.employeemgmt.employeeservice.event.EmployeeEvent;
import com.employeemgmt.employeeservice.kafka.KafkaProducerService;
import com.employeemgmt.employeeservice.repository.DepartmentRepository;
import com.employeemgmt.employeeservice.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final KafkaProducerService kafkaProducerService;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           KafkaProducerService kafkaProducerService) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public EmployeeResponse createEmployee(EmployeeRequest request, String createdBy) {
        // Validate unique constraints
        if (employeeRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new RuntimeException("Employee ID already exists: " + request.getEmployeeId());
        }

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Create employee
        Employee employee = new Employee();
        employee.setEmployeeId(request.getEmployeeId());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setPosition(request.getPosition());
        employee.setSalary(request.getSalary());
        employee.setStatus(request.getStatus());
        employee.setHireDate(request.getHireDate());
        employee.setCreatedBy(createdBy);

        // Set department if provided
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            employee.setDepartment(department);
        }

        employee = employeeRepository.save(employee);

        // Publish Kafka event
        publishEmployeeEvent(employee, com.employeemgmt.employeeservice.event.EventType.EMPLOYEE_CREATED, createdBy);

        return mapToResponse(employee);
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request, String updatedBy) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Check if email changed and already exists
        if (!employee.getEmail().equals(request.getEmail()) &&
                employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Update fields
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setPosition(request.getPosition());
        employee.setSalary(request.getSalary());
        employee.setStatus(request.getStatus());
        employee.setHireDate(request.getHireDate());

        // Update department
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            employee.setDepartment(department);
        } else {
            employee.setDepartment(null);
        }

        employee = employeeRepository.save(employee);

        // Publish Kafka event
        publishEmployeeEvent(employee, com.employeemgmt.employeeservice.event.EventType.EMPLOYEE_UPDATED, updatedBy);

        return mapToResponse(employee);
    }

    public void deleteEmployee(Long id, String deletedBy) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Publish Kafka event before deletion
        publishEmployeeEvent(employee, com.employeemgmt.employeeservice.event.EventType.EMPLOYEE_DELETED, deletedBy);

        employeeRepository.delete(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return mapToResponse(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
        return mapToResponse(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByDepartment(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByStatus(EmployeeStatus status) {
        return employeeRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> searchEmployeesByName(String name) {
        return employeeRepository.searchByName(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void publishEmployeeEvent(Employee employee, com.employeemgmt.employeeservice.event.EventType eventType, String performedBy) {
        EmployeeEvent event = EmployeeEvent.builder()
                .eventType(eventType)
                .employeeId(employee.getId())
                .employeeIdCode(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .position(employee.getPosition())
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .salary(employee.getSalary())
                .status(employee.getStatus())
                .performedBy(performedBy)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaProducerService.sendEmployeeEvent(event);
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .position(employee.getPosition())
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .salary(employee.getSalary())
                .status(employee.getStatus())
                .hireDate(employee.getHireDate())
                .createdBy(employee.getCreatedBy())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
