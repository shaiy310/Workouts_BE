package com.YaakoviCorp.Fit1337.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record Workout(
        UUID id,
        UUID ownerId,          // Ready for ownership & future sharing
        String name,
        String description,
        String notes,
        Integer duration,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<WorkoutSet> sets
) {
    public Workout {
        sets = sets == null ? List.of() : List.copyOf(sets);
    }
}