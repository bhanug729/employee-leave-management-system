package com.bhanu.leave.service;

import com.bhanu.leave.dto.request.LeaveRequest;
import com.bhanu.leave.dto.response.LeaveResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface LeaveService {
    @Nullable LeaveResponse applyLeave(@Valid LeaveRequest request);
    @Nullable LeaveResponse getLeaveById(Long id);
    @Nullable List<LeaveResponse> getAllLeaves();
    @Nullable List<LeaveResponse> getLeavesByEmployee(String employeeCode);
    @Nullable LeaveResponse approveLeave(Long id, String approvedBy);
    @Nullable LeaveResponse rejectLeave(Long id, String rejectedBy);
    @Nullable LeaveResponse cancelLeave(Long id);
}
