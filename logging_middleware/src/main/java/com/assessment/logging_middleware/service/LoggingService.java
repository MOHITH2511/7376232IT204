package com.assessment.logging_middleware.service;

import com.assessment.logging_middleware.model.LogRequest;
import com.assessment.logging_middleware.model.LogResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class LoggingService {

    private static final String LOG_API_URL =
            "http://4.224.186.213/evaluation-service/logs";

    @Value("${auth.token}")
    private String authToken;

    private final RestTemplate restTemplate;

    public LoggingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void Log(String stack, String level, 
                    String packageName, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(authToken);

            LogRequest logRequest = new LogRequest(
                stack, level, packageName, message
            );

            HttpEntity<LogRequest> request = 
                new HttpEntity<>(logRequest, headers);

            ResponseEntity<LogResponse> response = restTemplate.postForEntity(
                LOG_API_URL,
                request,
                LogResponse.class
            );

            log.debug("Log sent successfully | logID={} | stack={} " +
                "level={} package={} message={}",
                response.getBody() != null ? 
                    response.getBody().getLogID() : "unknown",
                stack, level, packageName, message);

        } catch (Exception e) {
            log.error("Failed to send log to evaluation server: {}", 
                e.getMessage());
        }
    }
}