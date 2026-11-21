package com.YaakoviCorp.Fit1337.repository;

import com.YaakoviCorp.Fit1337.model.ExerciseWeight;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExerciseWeightRepository {
    List<ExerciseWeight> findAllByUserId(UUID userId);
    Optional<ExerciseWeight> findByUserIdAndExerciseName(UUID userId, String exerciseName);
    ExerciseWeight upsertWeight(UUID userId, String exerciseName, double weight);
    boolean deleteByUserIdAndExerciseName(UUID userId, String exerciseName);
}