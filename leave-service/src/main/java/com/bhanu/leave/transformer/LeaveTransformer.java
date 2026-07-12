package com.bhanu.leave.transformer;

import com.bhanu.leave.dto.request.LeaveRequest;
import com.bhanu.leave.dto.response.EmployeeResponse;
import com.bhanu.leave.dto.response.LeaveResponse;
import com.bhanu.leave.entity.LeaveApplication;
import com.bhanu.leave.enums.LeaveStatus;

import java.util.UUID;

public class LeaveTransformer {

    public static LeaveResponse leaveToLeaveResponse(LeaveApplication leave) {
        return LeaveResponse.builder()
                .id(leave.getId())
                .leaveRequestNumber(leave.getLeaveRequestNumber())
                .employeeCode(leave.getEmployeeCode())
                .employeeName(leave.getEmployeeName())
                .leaveType(leave.getLeaveType())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .numberOfDays(leave.getNumberOfDays())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .appliedDate(leave.getAppliedDate())
                .approvedBy(leave.getApprovedBy())
                .build();
    }

    public static LeaveApplication leaveRequestToLeave(LeaveRequest leaveRequest, EmployeeResponse employeeResponse, int numberOfDays) {
        return LeaveApplication.builder()
                .leaveRequestNumber("LR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .employeeCode(employeeResponse.getEmployeeCode())
                .employeeName(employeeResponse.getFirstName() + " " + employeeResponse.getLastName())
                .leaveType(leaveRequest.getLeaveType())
                .startDate(leaveRequest.getStartDate())
                .endDate(leaveRequest.getEndDate())
                .numberOfDays(numberOfDays)
                .reason(leaveRequest.getReason())
                .status(LeaveStatus.PENDING)
                .build();
    }
}
