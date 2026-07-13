package com.bhanu.leave.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * The @LoadBalanced annotation is what makes RestTemplate "Eureka-aware":
 * instead of a real host/port, EmployeeClient calls http://employee-service/...
 * and Spring Cloud LoadBalancer resolves that logical name to a live
 * instance address at request time, the same way Feign does under the hood.
 */
@Configuration
public class RestTemplateConfig {

    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
