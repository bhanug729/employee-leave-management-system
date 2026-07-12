package com.bhanu.leave.service;

import com.bhanu.leave.client.EmployeeClient;
import com.bhanu.leave.dto.request.LeaveRequest;
import com.bhanu.leave.dto.response.EmployeeResponse;
import com.bhanu.leave.dto.response.LeaveResponse;
import com.bhanu.leave.entity.LeaveApplication;
import com.bhanu.leave.enums.LeaveStatus;
import com.bhanu.leave.exception.InsufficientLeaveBalanceException;
import com.bhanu.leave.exception.InvalidLeaveStateException;
import com.bhanu.leave.exception.LeaveNotFoundException;
import com.bhanu.leave.repository.LeaveRepository;
import com.bhanu.leave.transformer.LeaveTransformer;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Implements a small leave-approval state machine:
 *   apply (PENDING) -> approve (APPROVED, balance deducted)
 *                    -> reject (REJECTED, no balance change - it was never deducted)
 *   APPROVED -> cancel (CANCELLED, balance restored)
 *   PENDING  -> cancel (CANCELLED, no balance change)
 * The leave balance is only touched at approval time, not at apply time,
 * which mirrors how most real leave-management workflows behave.
 */
@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService{
    private static final Logger log = LoggerFactory.getLogger(LeaveServiceImpl.class);
    private final LeaveRepository leaveRepository;

    private final EmployeeClient employeeClient;

    @Override
    public @Nullable LeaveResponse applyLeave(LeaveRequest request) {
        log.info("Applying leave for employee code: {} from {} to {}",
                request.getEmployeeCode(), request.getStartDate(), request.getEndDate());

        EmployeeResponse employeeResponse = employeeClient.getEmployeeByCode(request.getEmployeeCode());

        long numberOfDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;
        if (numberOfDays <= 0) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }

        if (numberOfDays > employeeResponse.getAnnualLeaveBalance()) {
            log.error("Insufficient leave balance for employee code: {}. Balance: {}, Requested: {}",
                    request.getEmployeeCode(), employeeResponse.getAnnualLeaveBalance(), numberOfDays);
            throw new InsufficientLeaveBalanceException(
                    "Insufficient leave balance. Available: " + employeeResponse.getAnnualLeaveBalance() + " days");
        }

        LeaveApplication leave = LeaveTransformer.leaveRequestToLeave(request, employeeResponse, (int)numberOfDays);

        LeaveApplication savedLeave = leaveRepository.save(leave);
        log.info("Leave application created: {}", savedLeave.getLeaveRequestNumber());
        return LeaveTransformer.leaveToLeaveResponse(savedLeave);
    }


    @Override
    public LeaveResponse approveLeave(Long id, String approvedBy) {
        log.info("Approving leave id: {}", id);
        LeaveApplication leave = findLeaveOrThrow(id);

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidLeaveStateException("Only pending leave requests can be approved");
        }

        employeeClient.deductLeave(leave.getEmployeeCode(), leave.getNumberOfDays());

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setApprovedBy(approvedBy);
        LeaveApplication updated = leaveRepository.save(leave);
        log.info("Leave approved: {}", leave.getLeaveRequestNumber());
        return LeaveTransformer.leaveToLeaveResponse(updated);
    }

    @Override
    public LeaveResponse rejectLeave(Long id, String rejectedBy) {
        log.info("Rejecting leave id: {}", id);
        LeaveApplication leave = findLeaveOrThrow(id);

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidLeaveStateException("Only pending leave requests can be rejected");
        }

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setApprovedBy(rejectedBy);
        LeaveApplication updated = leaveRepository.save(leave);
        log.info("Leave rejected: {}", leave.getLeaveRequestNumber());
        return LeaveTransformer.leaveToLeaveResponse(updated);
    }

    @Override
    public LeaveResponse cancelLeave(Long id) {
        log.info("Cancelling leave id: {}", id);
        LeaveApplication leave = findLeaveOrThrow(id);

        if (leave.getStatus() == LeaveStatus.APPROVED && leave.getStartDate().isAfter(LocalDate.now())) {
            employeeClient.restoreLeave(leave.getEmployeeCode(), leave.getNumberOfDays());
        }

        leave.setStatus(LeaveStatus.CANCELLED);
        LeaveApplication updated = leaveRepository.save(leave);
        log.info("Leave cancelled: {}", leave.getLeaveRequestNumber());
        return LeaveTransformer.leaveToLeaveResponse(updated);
    }

    @Override
    public LeaveResponse getLeaveById(Long id) {
        return LeaveTransformer.leaveToLeaveResponse(findLeaveOrThrow(id));
    }

    @Override
    public List<LeaveResponse> getAllLeaves() {
        return leaveRepository.findAll().stream().map(LeaveTransformer::leaveToLeaveResponse).toList();
    }

    @Override
    public List<LeaveResponse> getLeavesByEmployee(String employeeCode) {
        return leaveRepository.findByEmployeeCode(employeeCode).stream().map(LeaveTransformer::leaveToLeaveResponse).toList();
    }

    private LeaveApplication findLeaveOrThrow(Long id) {
        return leaveRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Leave application not found with id: {}", id);
                    return new LeaveNotFoundException("Leave application not found with id: " + id);
                });
    }
}
