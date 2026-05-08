package com.vechile.vechilescheduler.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

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

            Map<String, String> body = Map.of(
                    "stack", stack,
                    "level", level,
                    "package", packageName,
                    "message", message
            );

            HttpEntity<Map<String, String>> request =
                    new HttpEntity<>(body, headers);

            restTemplate.postForEntity(
                    LOG_API_URL, request, String.class
            );

            log.debug("Log sent | stack={} level={} package={} message={}",
                    stack, level, packageName, message);

        } catch (Exception e) {
            log.error("Failed to send log: {}", e.getMessage());
        }
    }
}