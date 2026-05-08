package com.vechile.vechilescheduler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Vehicle {

    @JsonProperty("TaskID")
    private String taskId;

    @JsonProperty("Duration")
    private int duration;

    @JsonProperty("Impact")
    private int impact;
}