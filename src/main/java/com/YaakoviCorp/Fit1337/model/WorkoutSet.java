package com.YaakoviCorp.Fit1337.model;

import java.util.List;

public record WorkoutSet(
        int setNumber,
        int rest,
        List<ExerciseSet> exercises
) {
    // Defensive copy ensures true immutability
    public WorkoutSet {
        exercises = exercises == null ? List.of() : List.copyOf(exercises);
    }
}