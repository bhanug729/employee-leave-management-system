package com.bhanu.employee.service;

import com.bhanu.employee.dto.request.EmployeeRequest;
import com.bhanu.employee.dto.response.EmployeeResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface EmployeeService {
    EmployeeResponse createEmployee(@Valid EmployeeRequest request);
    @Nullable EmployeeResponse getEmployeeById(Long id);
    @Nullable EmployeeResponse getEmployeeByCode(String employeeCode);
    @Nullable List<EmployeeResponse> getAllEmployees();
    @Nullable EmployeeResponse updateEmployee(Long id, @Valid EmployeeRequest request);
    void deleteEmployee(Long id);
    void deductLeave(Long id, Integer days);
    void restoreLeave(Long id, Integer days);
}
