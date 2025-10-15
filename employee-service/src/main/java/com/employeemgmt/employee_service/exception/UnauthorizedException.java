package com.employeemgmt.employeeservice.exception;

/**
 * Custom exception for unauthorized access attempts
 * Thrown when user doesn't have permission for an operation
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}