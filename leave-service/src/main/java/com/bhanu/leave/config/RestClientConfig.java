package com.bhanu.leave.config;

import com.bhanu.leave.exception.EmployeeNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

import java.io.IOException;

/**
 * The @LoadBalanced annotation is what makes RestClient "Eureka-aware":
 * instead of a real host/port, EmployeeClient calls http://employee-service/...
 * and Spring Cloud LoadBalancer resolves that logical name to a live
 * instance address at request time, the same way Feign does under the hood.
 */
@Configuration
public class RestClientConfig {

    @Bean
    @Primary
    public RestClient.Builder defaultRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    @LoadBalanced
    @Qualifier("lbBuilder")
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient employeeRestClient(@Qualifier("lbBuilder") RestClient.Builder builder) {
        return builder
                .baseUrl("http://employee-service")
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, this::handle4xxClientError)
                .build();
    }

    private void handle4xxClientError(HttpRequest request, ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().value() == 404) {
            throw new EmployeeNotFoundException("Employee not found at: " + request.getURI());
        }
        throw new RuntimeException("Client Error: " + response.getStatusCode());
    }
}
