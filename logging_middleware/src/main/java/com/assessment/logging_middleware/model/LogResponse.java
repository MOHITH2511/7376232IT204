package com.assessment.logging_middleware.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LogResponse {
    private String logID;
    private String message;
}