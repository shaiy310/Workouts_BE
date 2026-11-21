package com.YaakoviCorp.Fit1337.model;

import java.time.OffsetDateTime;

public record ExerciseWeight(
        String exerciseName,
        double weight,
        OffsetDateTime updated_at
) { }

