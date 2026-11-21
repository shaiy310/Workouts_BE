
package com.YaakoviCorp.Fit1337.service;

import com.YaakoviCorp.Fit1337.model.ExerciseWeight;
import com.YaakoviCorp.Fit1337.model.WeightChangeEvent;
import com.YaakoviCorp.Fit1337.repository.ExerciseWeightRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ExerciseWeightService{
    private final ExerciseWeightRepository repo;
    private final ActivityEventService eventService;

    public ExerciseWeightService(ExerciseWeightRepository repo,  ActivityEventService activityEventService) {
        this.repo = repo;
        this.eventService = activityEventService;
    }

    @Transactional(readOnly = true)
    public List<ExerciseWeight> getUserWeights(UUID userId) {
        return repo.findAllByUserId(userId);
    }

    @Transactional
    public ExerciseWeight updateWeight(UUID userId, String exerciseName, double weight) {
        String normalizedExercise = normalizeExerciseName(exerciseName);
        if (Double.isNaN(weight) || weight < 0 || weight > 40) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "weight must be between 0 and 40");
        }

        double previousWeight = repo.findByUserIdAndExerciseName(userId, normalizedExercise)
                .map(ExerciseWeight::weight)
                .orElse(0.0);

        ExerciseWeight updated = repo.upsertWeight(userId, normalizedExercise, weight);

        if (Double.compare(previousWeight, weight) != 0) {
            WeightChangeEvent event = new WeightChangeEvent(
                    null,
                    LocalDate.now(),
                    normalizedExercise,
                    previousWeight,
                    weight
            );
            eventService.recordEvent(event, userId);
        }

        return updated;
    }

    @Transactional
    public void deleteWeight(UUID userId, String exerciseName) {
        String normalizedExercise = normalizeExerciseName(exerciseName);
        boolean deleted = repo.deleteByUserIdAndExerciseName(userId, normalizedExercise);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Exercise weight not found");
        }
    }

    private String normalizeExerciseName(String exerciseName) {
        if (exerciseName == null || exerciseName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "exerciseName is required");
        }
        return exerciseName.trim();
    }
}