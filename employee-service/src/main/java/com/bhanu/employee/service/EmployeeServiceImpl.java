package com.bhanu.employee.service;

import com.bhanu.employee.dto.request.EmployeeRequest;
import com.bhanu.employee.dto.response.EmployeeResponse;
import com.bhanu.employee.entity.Employee;
import com.bhanu.employee.exception.DuplicateEmployeeException;
import com.bhanu.employee.exception.EmployeeNotFoundException;
import com.bhanu.employee.exception.InsufficientLeaveBalanceException;
import com.bhanu.employee.repository.EmployeeRepository;
import com.bhanu.employee.transformer.EmployeeTransformer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        log.info("Creating employee with code: {}", request.getEmployeeCode());
        if (employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            log.error("Duplicate employee code: {}", request.getEmployeeCode());
            throw new DuplicateEmployeeException("Employee with code " + request.getEmployeeCode() + " already exists");
        }
        if (employeeRepository.existsByEmail(request.getEmail())) {
            log.error("Duplicate employee email: {}", request.getEmail());
            throw new DuplicateEmployeeException("Employee with email " + request.getEmail() + " already exists");
        }
        Employee employee = EmployeeTransformer.employeeRequestToEmployee(request);
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee created with code: {}", savedEmployee.getEmployeeCode());
        return EmployeeTransformer.employeetoEmployeeResponse(savedEmployee);
    }

    @Override
    public @Nullable EmployeeResponse getEmployeeById(Long id) {
        log.debug("Fetching employee with id: {}", id);
        Employee employee = findEmployeeOrThrow(id);
        return EmployeeTransformer.employeetoEmployeeResponse(employee);
    }

    @Override
    public @Nullable EmployeeResponse getEmployeeByCode(String employeeCode) {
        log.debug("Fetching employee with code: {}", employeeCode);
        Employee employee = findEmployeeByCodeOrThrow(employeeCode);
        return EmployeeTransformer.employeetoEmployeeResponse(employee);
    }

    @Override
    public @Nullable List<EmployeeResponse> getAllEmployees() {
        log.debug("Fetching all employees");
        return employeeRepository.findAll()
                .stream()
                .map(employee -> EmployeeTransformer.employeetoEmployeeResponse(employee))
                .toList();
    }

    @Override
    public @Nullable EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        log.info("Updating employee with id: {}", id);
        Employee employee = findEmployeeOrThrow(id);
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setDepartment(request.getDepartment());
        employee.setDesignation(request.getDesignation());
        employee.setDateOfJoining(request.getDateOfJoining());
        Employee updated = employeeRepository.save(employee);
        log.info("Employee updated for given id: {}", updated.getId());
        return EmployeeTransformer.employeetoEmployeeResponse(updated);
    }

    @Override
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with id: {}", id);
        Employee employee = findEmployeeOrThrow(id);
        employeeRepository.delete(employee);
        log.info("Employee deleted for given id: {}", id);
    }

    @Override
    @Transactional
    public void deductLeave(String employeeCode, Integer days) {
        log.info("Deducting {} leave day(s) for employee code: {}", days, employeeCode);
        Employee employee = findEmployeeByCodeOrThrow(employeeCode);
        if (employee.getAnnualLeaveBalance() < days) {
            log.error("Insufficient leave balance for employee code: {}. Balance: {}, Requested: {}",
                    employeeCode, employee.getAnnualLeaveBalance(), days);
            throw new InsufficientLeaveBalanceException("Insufficient leave balance for employee: " + employeeCode);
        }
        employee.setAnnualLeaveBalance(employee.getAnnualLeaveBalance() - days);
        employeeRepository.save(employee);
        log.info("Leave deducted for employee code: {}. New balance: {}", employeeCode, employee.getAnnualLeaveBalance());
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void restoreLeave(String employeeCode, Integer days) {
        log.info("Restoring {} leave day(s) for employee code: {}", days, employeeCode);
        Employee employee = findEmployeeByCodeOrThrow(employeeCode);
        employee.setAnnualLeaveBalance(employee.getAnnualLeaveBalance() + days);
        employeeRepository.save(employee);
        log.info("Leave restored for employee code: {}. New balance: {}", employeeCode, employee.getAnnualLeaveBalance());
    }

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with id: {}", id);
                    return new EmployeeNotFoundException("Employee not found with id: " + id);
                });
    }

    private Employee findEmployeeByCodeOrThrow(String employeeCode) {
        return employeeRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() -> {
                    log.error("Employee not found with code: {}", employeeCode);
                    return new EmployeeNotFoundException("Employee not found with code: " + employeeCode);
                });
    }
}
