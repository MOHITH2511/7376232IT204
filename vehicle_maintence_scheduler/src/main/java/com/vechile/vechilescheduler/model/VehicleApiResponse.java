package com.vechile.vechilescheduler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class VehicleApiResponse {

    @JsonProperty("vehicles")
    private List<Vehicle> vehicles;
}