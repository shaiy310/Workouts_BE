package com.YaakoviCorp.Fit1337.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record WeightChangeEvent(
        UUID id,
        LocalDate date,
        @NotBlank(message = "exerciseName is required")
        @Size(max = 200, message = "exerciseName must be at most 200 characters")
        String exerciseName,
        @DecimalMin(value = "0.0", message = "prevValue must be at least 0")
        @DecimalMax(value = "40.0", message = "prevValue must be at most 40")
        double prevWeight,
        @DecimalMin(value = "0.0", message = "newValue must be at least 0")
        @DecimalMax(value = "40.0", message = "newValue must be at most 40")
        double newWeight
) implements ActivityEvent {
    @Override
    public String type() {
        return "weight";
    }
}

