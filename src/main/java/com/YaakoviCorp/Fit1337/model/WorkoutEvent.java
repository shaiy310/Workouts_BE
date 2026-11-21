package com.YaakoviCorp.Fit1337.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record WorkoutEvent(
        UUID id,
        LocalDate date,
        @NotBlank(message = "workoutName is required")
        @Size(max = 200, message = "workoutName must be at most 200 characters")
        String workoutName,
        @Size(max = 2000, message = "workoutDescription must be at most 2000 characters")
        String workoutDescription
) implements ActivityEvent {
    @Override
    public String type() {
        return "workout";
    }
}
