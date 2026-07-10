package com.bhanu.employee.controller;

import com.bhanu.employee.dto.request.EmployeeRequest;
import com.bhanu.employee.dto.response.EmployeeResponse;
import com.bhanu.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;

    @PostMapping("/add")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        log.info("Received request to create employee: {}", request.getEmployeeCode());
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable("id") Long id) {
        log.info("Received request to fetch employee with id: {}", id);
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/code/{employeeCode}")
    public ResponseEntity<EmployeeResponse> getEmployeeByCode(@PathVariable("employeeCode") String employeeCode) {
        log.info("Received request to fetch employee with code: {}", employeeCode);
        return ResponseEntity.ok(employeeService.getEmployeeByCode(employeeCode));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        log.info("Received request to fetch all employees");
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable("id") Long id,
                                                           @Valid @RequestBody EmployeeRequest request) {
        log.info("Received request to update employee with id: {}", id);
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long id) {
        log.info("Received request to delete employee with id: {}", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    /** Called internally by leave-service when a leave request is approved. */
    @PutMapping("/{id}/deduct-leave")
    public ResponseEntity<Void> deductLeave(@PathVariable("id") Long id,
                                            @RequestParam("days") Integer days) {
        log.info("Received internal request to deduct {} leave day(s) for employee id: {}", days, id);
        employeeService.deductLeave(id, days);
        return ResponseEntity.ok().build();
    }

    /** Called internally by leave-service when an approved leave is later cancelled. */
    @PutMapping("/{id}/restore-leave")
    public ResponseEntity<Void> restoreLeave(@PathVariable("id") Long id,
                                             @RequestParam("days") Integer days) {
        log.info("Received internal request to restore {} leave day(s) for employee id: {}", days, id);
        employeeService.restoreLeave(id, days);
        return ResponseEntity.ok().build();
    }
}
