package com.vechile.vechilescheduler.service;

import com.vechile.vechilescheduler.model.Vehicle;
import com.vechile.vechilescheduler.model.VehicleApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class VehicleService {

    private static final String VEHICLE_API_URL =
            "http://4.224.186.213/evaluation-service/vehicles";

    @Value("${auth.token}")
    private String authToken;

    private final RestTemplate restTemplate;
    private final LoggingService loggingService;

    public VehicleService(RestTemplate restTemplate,
                          LoggingService loggingService) {
        this.restTemplate = restTemplate;
        this.loggingService = loggingService;
    }

    public List<Vehicle> fetchVehicles() {
        loggingService.Log("backend", "info", "service",
                "Fetching vehicles from evaluation API");
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authToken);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<VehicleApiResponse> response =
                    restTemplate.exchange(
                            VEHICLE_API_URL,
                            HttpMethod.GET,
                            request,
                            VehicleApiResponse.class
                    );

            List<Vehicle> vehicles = response.getBody() != null
                    ? response.getBody().getVehicles()
                    : Collections.emptyList();

            loggingService.Log("backend", "info", "service",
                    "Successfully fetched " + vehicles.size() + " vehicles");

            return vehicles;

        } catch (Exception e) {
            loggingService.Log("backend", "error", "service",
                    "Failed to fetch vehicles: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}