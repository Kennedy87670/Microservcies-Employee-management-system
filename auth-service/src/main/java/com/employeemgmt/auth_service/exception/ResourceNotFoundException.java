package com.employeemgmt.auth_service.exception;

/**
 * Custom exception for resource not found scenarios
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}