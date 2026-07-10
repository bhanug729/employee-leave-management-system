package com.bhanu.employee.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String string) {
        super(string);
    }
}
