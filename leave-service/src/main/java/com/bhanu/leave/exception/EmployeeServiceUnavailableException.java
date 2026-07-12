package com.bhanu.leave.exception;

public class EmployeeServiceUnavailableException extends RuntimeException {
    public EmployeeServiceUnavailableException(String message) {
        super(message);
    }
}
