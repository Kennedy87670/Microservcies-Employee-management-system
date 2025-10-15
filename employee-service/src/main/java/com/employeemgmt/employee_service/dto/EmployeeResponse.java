package com.employeemgmt.employeeservice.dto;

import com.employeemgmt.employeeservice.entity.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String position;
    private Long departmentId;
    private String departmentName;
    private BigDecimal salary;
    private EmployeeStatus status;
    private LocalDate hireDate;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}