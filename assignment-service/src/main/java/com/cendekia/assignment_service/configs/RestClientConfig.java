package com.cendekia.assignment_service.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${enrollment.service.url:http://enrollment-service:8003}")
    private String enrollmentServiceUrl;

    @Bean
    public RestClient enrollmentRestClient() {
        return RestClient.builder()
                .baseUrl(enrollmentServiceUrl)
                .build();
    }
}
