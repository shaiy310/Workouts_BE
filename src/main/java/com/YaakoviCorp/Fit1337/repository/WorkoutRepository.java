package com.YaakoviCorp.Fit1337.repository;

import com.YaakoviCorp.Fit1337.model.Workout;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutRepository {
    Workout createWorkout(Workout workout);
    Optional<Workout> updateWorkout(Workout workout);
    Optional<Workout> findByIdAndOwnerId(UUID id, UUID ownerId);
    boolean deleteByIdAndOwnerId(UUID id, UUID ownerId);

    List<Workout> searchWorkouts(UUID ownerId, String name, String description, Integer maxDuration);
}

