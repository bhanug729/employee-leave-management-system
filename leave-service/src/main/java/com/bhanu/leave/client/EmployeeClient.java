package com.bhanu.leave.client;

import com.bhanu.leave.dto.response.EmployeeResponse;
import com.bhanu.leave.exception.EmployeeNotFoundException;
import com.bhanu.leave.exception.EmployeeServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class EmployeeClient {
    private static final Logger log = LoggerFactory.getLogger(EmployeeClient.class);
    private static final String SERVICE_URL = "http://localhost:8080/employee";

    private final RestTemplate restTemplate;

    public EmployeeResponse getEmployeeByCode(String employeeCode) {
        try {
            log.debug("Calling employee-service to fetch employee code: {}", employeeCode);
            return restTemplate.getForObject(SERVICE_URL + "/code/" + employeeCode, EmployeeResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Employee not found with code: {}", employeeCode);
            throw new EmployeeNotFoundException("Employee not found with code: " + employeeCode);
        } catch (ResourceAccessException e) {
            log.error("Employee service is unavailable", e);
            throw new EmployeeServiceUnavailableException("Employee service is currently unavailable");
        }
    }

    public void deductLeave(String employeeCode, Integer days) {
        try {
            log.debug("Calling employee-service to deduct {} leave day(s) for employee code: {}", days, employeeCode);
            String url = SERVICE_URL + "/" + employeeCode + "/deduct-leave?days=" + days;
            restTemplate.put(url, null);
        } catch (HttpClientErrorException | ResourceAccessException e) {
            log.error("Failed to deduct leave for employee code: {}", employeeCode, e);
            throw new EmployeeServiceUnavailableException("Unable to update leave balance at this time");
        }
    }

    public void restoreLeave(String employeeCode, Integer days) {
        try {
            log.debug("Calling employee-service to restore {} leave day(s) for employee id: {}", days, employeeCode);
            String url = SERVICE_URL + "/" + employeeCode + "/restore-leave?days=" + days;
            restTemplate.put(url, null);
        } catch (HttpClientErrorException | ResourceAccessException e) {
            log.error("Failed to restore leave for employee id: {}", employeeCode, e);
        }
    }
}
