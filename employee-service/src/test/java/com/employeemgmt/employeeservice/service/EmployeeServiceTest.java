package com.employeemgmt.employeeservice.service;

import com.employeemgmt.employeeservice.dto.EmployeeRequest;
import com.employeemgmt.employeeservice.dto.EmployeeResponse;
import com.employeemgmt.employeeservice.entity.Department;
import com.employeemgmt.employeeservice.entity.Employee;
import com.employeemgmt.employeeservice.entity.EmployeeStatus;
import com.employeemgmt.employeeservice.kafka.KafkaProducerService;
import com.employeemgmt.employeeservice.repository.DepartmentRepository;
import com.employeemgmt.employeeservice.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeRequest employeeRequest;
    private Employee employee;
    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Engineering");

        employeeRequest = new EmployeeRequest();
        employeeRequest.setEmployeeId("EMP001");
        employeeRequest.setFirstName("John");
        employeeRequest.setLastName("Doe");
        employeeRequest.setEmail("john.doe@company.com");
        employeeRequest.setPosition("Developer");
        employeeRequest.setDepartmentId(1L);
        employeeRequest.setSalary(new BigDecimal("75000"));
        employeeRequest.setStatus(EmployeeStatus.ACTIVE);
        employeeRequest.setHireDate(LocalDate.now());

        employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeId("EMP001");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@company.com");
        employee.setPosition("Developer");
        employee.setDepartment(department);
        employee.setSalary(new BigDecimal("75000"));
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setHireDate(LocalDate.now());
    }

    @Test
    void shouldCreateEmployeeSuccessfully() {
        // Given
        when(employeeRepository.existsByEmployeeId("EMP001")).thenReturn(false);
        when(employeeRepository.existsByEmail("john.doe@company.com")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // When
        EmployeeResponse response = employeeService.createEmployee(employeeRequest, "admin");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getEmployeeId()).isEqualTo("EMP001");
        verify(employeeRepository).save(any(Employee.class));
        verify(kafkaProducerService).sendEmployeeEvent(any());
    }

    @Test
    void shouldThrowExceptionWhenEmployeeIdExists() {
        // Given
        when(employeeRepository.existsByEmployeeId("EMP001")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> employeeService.createEmployee(employeeRequest, "admin"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Employee ID already exists");
    }

    @Test
    void shouldUpdateEmployeeSuccessfully() {
        // Given
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // When
        EmployeeResponse response = employeeService.updateEmployee(1L, employeeRequest, "admin");

        // Then
        assertThat(response).isNotNull();
        verify(employeeRepository).save(any(Employee.class));
        verify(kafkaProducerService).sendEmployeeEvent(any());
    }

    @Test
    void shouldDeleteEmployeeSuccessfully() {
        // Given
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        // When
        employeeService.deleteEmployee(1L, "admin");

        // Then
        verify(employeeRepository).delete(employee);
        verify(kafkaProducerService).sendEmployeeEvent(any());
    }

    @Test
    void shouldGetEmployeeById() {
        // Given
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        // When
        EmployeeResponse response = employeeService.getEmployeeById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("John");
    }
}
