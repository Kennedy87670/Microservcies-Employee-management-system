package com.employeemgmt.employeeservice.repository;

import com.employeemgmt.employeeservice.entity.Department;
import com.employeemgmt.employeeservice.entity.Employee;
import com.employeemgmt.employeeservice.entity.EmployeeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department testDepartment;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testDepartment = new Department();
        testDepartment.setName("Engineering");
        testDepartment.setDescription("Software Development");
        testDepartment = departmentRepository.save(testDepartment);

        testEmployee = new Employee();
        testEmployee.setEmployeeId("EMP001");
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("john.doe@company.com");
        testEmployee.setPosition("Developer");
        testEmployee.setDepartment(testDepartment);
        testEmployee.setSalary(new BigDecimal("75000"));
        testEmployee.setStatus(EmployeeStatus.ACTIVE);
        testEmployee.setHireDate(LocalDate.now());
        testEmployee.setCreatedBy("admin");
    }

    @Test
    void shouldSaveEmployee() {
        // When
        Employee saved = employeeRepository.save(testEmployee);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmployeeId()).isEqualTo("EMP001");
    }

    @Test
    void shouldFindEmployeeByEmployeeId() {
        // Given
        employeeRepository.save(testEmployee);

        // When
        Optional<Employee> found = employeeRepository.findByEmployeeId("EMP001");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void shouldFindEmployeesByDepartment() {
        // Given
        employeeRepository.save(testEmployee);

        // When
        List<Employee> employees = employeeRepository.findByDepartmentId(testDepartment.getId());

        // Then
        assertThat(employees).hasSize(1);
        assertThat(employees.get(0).getDepartment().getName()).isEqualTo("Engineering");
    }

    @Test
    void shouldFindEmployeesByStatus() {
        // Given
        employeeRepository.save(testEmployee);

        // When
        List<Employee> active = employeeRepository.findByStatus(EmployeeStatus.ACTIVE);

        // Then
        assertThat(active).hasSize(1);
        assertThat(active.get(0).getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
    }

    @Test
    void shouldSearchEmployeesByName() {
        // Given
        employeeRepository.save(testEmployee);

        // When
        List<Employee> found = employeeRepository.searchByName("John");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getFirstName()).isEqualTo("John");
    }
}