package com.bhanu.leave.entity;

import com.bhanu.leave.enums.LeaveStatus;
import com.bhanu.leave.enums.LeaveType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String leaveRequestNumber;

    @Column(nullable = false)
    private String employeeCode;

    @Column(nullable = false)
    private String employeeName;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private Integer numberOfDays;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    private LocalDateTime appliedDate;

    private String approvedBy;

    @PrePersist
    protected void onCreate() {
        appliedDate = LocalDateTime.now();
    }
}
