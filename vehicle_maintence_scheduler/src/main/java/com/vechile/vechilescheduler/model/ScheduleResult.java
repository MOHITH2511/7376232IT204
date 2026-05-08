package com.vechile.vechilescheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleResult {

    private int depotId;
    private int mechanicHoursAvailable;
    private int totalDurationUsed;
    private int totalImpactScore;
    private List<Vehicle> selectedVehicles;
}