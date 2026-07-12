package com.bhanu.leave.repository;

import com.bhanu.leave.entity.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByEmployeeCode(String employeeCode);
}
