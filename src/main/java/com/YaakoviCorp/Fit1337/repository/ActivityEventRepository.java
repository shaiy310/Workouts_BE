package com.YaakoviCorp.Fit1337.repository;

import com.YaakoviCorp.Fit1337.model.ActivityEvent;
import com.YaakoviCorp.Fit1337.model.WeightChangeEvent;
import com.YaakoviCorp.Fit1337.model.WorkoutEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityEventRepository {
    List<ActivityEvent> findAllByUserId(UUID userId, LocalDate start, LocalDate end);
    Optional<WorkoutEvent> findWorkoutEventByUserAndDate(UUID userId, LocalDate date);
    Optional<WeightChangeEvent> findWeightEventByUserExerciseAndDate(UUID userId, String exerciseName, LocalDate date);
    WorkoutEvent saveWorkoutEvent(UUID userId, WorkoutEvent event);
    WorkoutEvent updateWorkoutEvent(UUID userId, WorkoutEvent event);
    WeightChangeEvent saveWeightEvent(UUID userId, WeightChangeEvent event);
    WeightChangeEvent updateWeightEvent(UUID userId, WeightChangeEvent event);
    void deleteByIdAndUserId(UUID id, UUID userId);
}

