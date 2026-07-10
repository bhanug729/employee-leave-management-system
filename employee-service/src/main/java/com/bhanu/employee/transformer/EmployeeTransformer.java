package com.bhanu.employee.transformer;

import com.bhanu.employee.dto.request.EmployeeRequest;
import com.bhanu.employee.dto.response.EmployeeResponse;
import com.bhanu.employee.entity.Employee;

public class EmployeeTransformer {

    public static EmployeeResponse employeetoEmployeeResponse(Employee employee) {
        return EmployeeResponse.builder()
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .dateOfJoining(employee.getDateOfJoining())
                .annualLeaveBalance(employee.getAnnualLeaveBalance())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }


    public static Employee employeeRequestToEmployee(EmployeeRequest employeeRequest) {
        return Employee.builder()
                .employeeCode(employeeRequest.getEmployeeCode())
                .firstName(employeeRequest.getFirstName())
                .lastName(employeeRequest.getLastName())
                .email(employeeRequest.getEmail())
                .department(employeeRequest.getDepartment())
                .designation(employeeRequest.getDesignation())
                .dateOfJoining(employeeRequest.getDateOfJoining())
                .build();
    }
}
