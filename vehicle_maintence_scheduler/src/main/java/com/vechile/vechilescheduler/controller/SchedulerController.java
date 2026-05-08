package com.vechile.vechilescheduler.controller;

import com.vechile.vechilescheduler.model.ScheduleResult;
import com.vechile.vechilescheduler.service.LoggingService;
import com.vechile.vechilescheduler.service.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/schedule")
public class SchedulerController {

    private final SchedulerService schedulerService;
    private final LoggingService loggingService;

    public SchedulerController(SchedulerService schedulerService,
                                LoggingService loggingService) {
        this.schedulerService = schedulerService;
        this.loggingService = loggingService;
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResult>> scheduleAll() {
        loggingService.Log("backend", "info", "route",
                "GET /api/schedule request received");

        List<ScheduleResult> results =
                schedulerService.scheduleForAllDepots();

        loggingService.Log("backend", "info", "route",
                "GET /api/schedule returning results for " +
                results.size() + " depots");

        return ResponseEntity.ok(results);
    }

    @GetMapping("/{depotId}")
    public ResponseEntity<?> scheduleForDepot(
            @PathVariable int depotId) {

        loggingService.Log("backend", "info", "route",
                "GET /api/schedule/" + depotId + " request received");

        ScheduleResult result =
                schedulerService.scheduleForDepot(depotId);

        if (result == null) {
            loggingService.Log("backend", "error", "route",
                    "Depot not found for depotId=" + depotId);
            return ResponseEntity.notFound().build();
        }

        loggingService.Log("backend", "info", "route",
                "GET /api/schedule/" + depotId +
                " returning result totalImpact=" +
                result.getTotalImpactScore());

        return ResponseEntity.ok(result);
    }
}