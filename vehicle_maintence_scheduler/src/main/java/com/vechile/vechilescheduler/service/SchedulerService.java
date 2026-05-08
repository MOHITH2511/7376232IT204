package com.vechile.vechilescheduler.service;

import com.vechile.vechilescheduler.model.Depot;
import com.vechile.vechilescheduler.model.ScheduleResult;
import com.vechile.vechilescheduler.model.Vehicle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SchedulerService {

    private final DepotService depotService;
    private final VehicleService vehicleService;
    private final LoggingService loggingService;

    public SchedulerService(DepotService depotService,
                            VehicleService vehicleService,
                            LoggingService loggingService) {
        this.depotService = depotService;
        this.vehicleService = vehicleService;
        this.loggingService = loggingService;
    }

    public List<ScheduleResult> scheduleForAllDepots() {
        loggingService.Log("backend", "info", "service",
                "Scheduling all depots");

        List<Depot> depots = depotService.fetchDepots();
        List<Vehicle> vehicles = vehicleService.fetchVehicles();

        if (depots.isEmpty()) {
            loggingService.Log("backend", "warn", "service",
                    "No depots found");
            return new ArrayList<>();
        }

        if (vehicles.isEmpty()) {
            loggingService.Log("backend", "warn", "service",
                    "No vehicles found");
            return new ArrayList<>();
        }

        List<ScheduleResult> results = new ArrayList<>();

        for (Depot depot : depots) {
            loggingService.Log("backend", "debug", "service",
                    "Processing depot=" + depot.getId());

            ScheduleResult result = runKnapsack(depot, vehicles);
            results.add(result);

            loggingService.Log("backend", "info", "service",
                    "Depot=" + depot.getId() +
                    " impact=" + result.getTotalImpactScore());
        }

        loggingService.Log("backend", "info", "service",
                "Scheduled " + results.size() + " depots");

        return results;
    }

    public ScheduleResult scheduleForDepot(int depotId) {
        loggingService.Log("backend", "info", "service",
                "Scheduling depot=" + depotId);

        List<Depot> depots = depotService.fetchDepots();
        List<Vehicle> vehicles = vehicleService.fetchVehicles();

        Depot targetDepot = depots.stream()
                .filter(d -> d.getId() == depotId)
                .findFirst()
                .orElse(null);

        if (targetDepot == null) {
            loggingService.Log("backend", "error", "service",
                    "Depot not found id=" + depotId);
            return null;
        }

        return runKnapsack(targetDepot, vehicles);
    }

    private ScheduleResult runKnapsack(Depot depot,
                                        List<Vehicle> vehicles) {
        int capacity = depot.getMechanicHours();
        int n = vehicles.size();

        loggingService.Log("backend", "debug", "service",
                "Knapsack depot=" + depot.getId() +
                " cap=" + capacity);

        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            Vehicle v = vehicles.get(i - 1);

            for (int w = 0; w <= capacity; w++) {
                dp[i][w] = dp[i - 1][w];
                if (v.getDuration() <= w) {
                    int withVehicle = dp[i - 1][w - v.getDuration()]
                            + v.getImpact();
                    if (withVehicle > dp[i][w]) {
                        dp[i][w] = withVehicle;
                    }
                }
            }
        }

        List<Vehicle> selected = new ArrayList<>();
        int w = capacity;

        for (int i = n; i > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                Vehicle v = vehicles.get(i - 1);
                selected.add(v);
                w -= v.getDuration();
            }
        }

        int totalDuration = selected.stream()
                .mapToInt(Vehicle::getDuration).sum();
        int totalImpact = selected.stream()
                .mapToInt(Vehicle::getImpact).sum();

        if (selected.isEmpty()) {
            loggingService.Log("backend", "warn", "service",
                    "No vehicles fit depot=" + depot.getId());
        } else {
            loggingService.Log("backend", "info", "service",
                    "Done depot=" + depot.getId() +
                    " impact=" + totalImpact);
        }

        return new ScheduleResult(
                depot.getId(),
                capacity,
                totalDuration,
                totalImpact,
                selected
        );
    }
}