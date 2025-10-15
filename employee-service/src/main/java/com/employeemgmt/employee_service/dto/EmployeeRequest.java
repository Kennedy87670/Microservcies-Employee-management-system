package com.employeemgmt.employeeservice.dto;

import com.employeemgmt.employeeservice.entity.EmployeeStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeRequest {

    @NotBlank(message = "Employee ID is required")
    @Size(max = 20, message = "Employee ID must not exceed 20 characters")
    private String employeeId;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @Size(max = 100, message = "Position must not exceed 100 characters")
    private String position;

    private Long departmentId;

    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
    private BigDecimal salary;

    @NotNull(message = "Status is required")
    private EmployeeStatus status;

    private LocalDate hireDate;
}