package com.bhanu.leave.controller;

import com.bhanu.leave.dto.request.LeaveRequest;
import com.bhanu.leave.dto.response.LeaveResponse;
import com.bhanu.leave.service.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
public class LeaveController {
    private static final Logger log = LoggerFactory.getLogger(LeaveController.class);
    private final LeaveService leaveService;

    @PostMapping("/apply")
    public ResponseEntity<LeaveResponse> applyLeave(@Valid @RequestBody LeaveRequest leaveRequest) {
        log.info("Received leave application request for employee code: {}", leaveRequest.getEmployeeCode());
        return new ResponseEntity<>(leaveService.applyLeave(leaveRequest), HttpStatus.CREATED);
        // return ResponseEntity.status(HttpStatus.CREATED).body(leaveService.applyLeave(leaveRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveResponse> getLeave(@PathVariable("id") Long id) {
        return ResponseEntity.ok(leaveService.getLeaveById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<LeaveResponse>> getAllLeaves() {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    @GetMapping("/employee/{employeeCode}")
    public ResponseEntity<List<LeaveResponse>> getLeavesByEmployee(@PathVariable("employeeCode") String employeeCode) {
        return ResponseEntity.ok(leaveService.getLeavesByEmployee(employeeCode));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveResponse> approveLeave(@PathVariable("id") Long id,
                                                      @RequestParam(value = "approvedBy", defaultValue = "Manager") String approvedBy) {
        log.info("Received request to approve leave id: {}", id);
        return ResponseEntity.ok(leaveService.approveLeave(id, approvedBy));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<LeaveResponse> rejectLeave(@PathVariable("id") Long id,
                                                     @RequestParam(value = "rejectedBy", defaultValue = "Manager") String rejectedBy) {
        log.info("Received request to reject leave id: {}", id);
        return ResponseEntity.ok(leaveService.rejectLeave(id, rejectedBy));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<LeaveResponse> cancelLeave(@PathVariable("id") Long id) {
        log.info("Received request to cancel leave id: {}", id);
        return ResponseEntity.ok(leaveService.cancelLeave(id));
    }
}
