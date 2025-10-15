package com.employeemgmt.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "ADMIN|MANAGER|EMPLOYEE", message = "Role must be ADMIN, MANAGER, or EMPLOYEE")
    private String role;
}