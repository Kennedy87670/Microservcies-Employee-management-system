package com.employeemgmt.employeeservice.event;

import com.employeemgmt.employeeservice.entity.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEvent {
    private String eventType; // CREATED, UPDATED, DELETED
    private Long employeeId;
    private String employeeIdCode;
    private String firstName;
    private String lastName;
    private String email;
    private String position;
    private Long departmentId;
    private String departmentName;
    private BigDecimal salary;
    private EmployeeStatus status;
    private String performedBy;
    private LocalDateTime timestamp;
}