package com.employeemgmt.employeeservice.exception;

/**
 * Custom exception thrown when a requested resource is not found
 * Used for employee, department, or other entity lookups
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
