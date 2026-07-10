package com.bhanu.employee.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String department;
    private String designation;
    private LocalDate dateOfJoining;
    private Integer annualLeaveBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

