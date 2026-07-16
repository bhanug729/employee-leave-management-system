package com.bhanu.leave.client;

import com.bhanu.leave.dto.response.EmployeeResponse;
import com.bhanu.leave.exception.EmployeeServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

/**
 * Imperative RestClient for employee-service, built on RestClient.
 * "employee-service" in the URL is a logical Eureka service ID; the
 * @LoadBalanced RestClient Builder resolves it to a real instance at call time.
 */
@Slf4j
@Component
public class EmployeeClient {
    private final RestClient restClient;
    public EmployeeClient(@Qualifier("employeeRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public EmployeeResponse getEmployeeByCode(String employeeCode) {
        try {
            log.debug("Calling employee-service to fetch employee code: {}", employeeCode);
            return restClient.get()
                    .uri("/employee/code/{code}", employeeCode)
                    .retrieve()
                    .body(EmployeeResponse.class);
        } catch (ResourceAccessException e) {
            log.error("Employee service is unavailable", e);
            throw new EmployeeServiceUnavailableException("Employee service is currently unavailable");
        }
    }

    public void deductLeave(String employeeCode, Integer days) {
        try {
            log.debug("Calling employee-service to deduct {} leave day(s) for employee code: {}", days, employeeCode);
            restClient.put()
                    .uri(uriBuilder -> uriBuilder
                            .path ("/employee/{code}/deduct-leave")
                            .queryParam("days", days)
                            .build(employeeCode))
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            log.error("Failed to deduct leave for employee code: {}", employeeCode, e);
            throw new EmployeeServiceUnavailableException("Unable to update leave balance at this time");
        }
    }

    public void restoreLeave(String employeeCode, Integer days) {
        try {
            log.debug("Calling employee-service to restore {} leave day(s) for employee id: {}", days, employeeCode);
            restClient.put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/employee/{code}/restore-leave")
                            .queryParam("days", days)
                            .build(employeeCode))
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            log.error("Failed to restore leave for employee id: {}", employeeCode, e);
        }
    }
}
