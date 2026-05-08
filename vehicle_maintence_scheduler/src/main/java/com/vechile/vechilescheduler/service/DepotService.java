package com.vechile.vechilescheduler.service;

import com.vechile.vechilescheduler.model.Depot;
import com.vechile.vechilescheduler.model.DepotApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class DepotService {

    private static final String DEPOT_API_URL =
            "http://4.224.186.213/evaluation-service/depots";

    @Value("${auth.token}")
    private String authToken;

    private final RestTemplate restTemplate;
    private final LoggingService loggingService;

    public DepotService(RestTemplate restTemplate,
                        LoggingService loggingService) {
        this.restTemplate = restTemplate;
        this.loggingService = loggingService;
    }

    public List<Depot> fetchDepots() {
        loggingService.Log("backend", "info", "service",
                "Fetching depots from evaluation API");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authToken);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<DepotApiResponse> response =
                    restTemplate.exchange(
                            DEPOT_API_URL,
                            HttpMethod.GET,
                            request,
                            DepotApiResponse.class
                    );

            List<Depot> depots = response.getBody() != null
                    ? response.getBody().getDepots()
                    : Collections.emptyList();

            loggingService.Log("backend", "info", "service",
                    "Successfully fetched " + depots.size() + " depots");

            return depots;

        } catch (Exception e) {
            loggingService.Log("backend", "error", "service",
                    "Failed to fetch depots: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}