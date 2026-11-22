package com.YaakoviCorp.Fit1337.service;

import com.YaakoviCorp.Fit1337.model.Workout;
import com.YaakoviCorp.Fit1337.repository.WorkoutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class WorkoutService{
    private static final Logger log = LoggerFactory.getLogger(WorkoutService.class);

    private final WorkoutRepository repo;

    WorkoutService(WorkoutRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Workout createWorkout(Workout workout, UUID authenticatedUserId) {
        validateWorkoutPayload(workout, authenticatedUserId);
        Workout ownedWorkout = new Workout(
                null,
                authenticatedUserId,
                workout.name(),
                workout.description(),
                workout.notes(),
                workout.duration(),
                null,
                null,
                workout.sets()
        );

        Workout created = repo.createWorkout(ownedWorkout);
        log.info("audit=workout_create outcome=success workoutId={} workoutName={}", workout.id(), workout.name());
        return created;
    }

    @Transactional
    public Workout updateWorkout(UUID id, Workout incoming, UUID authenticatedUserId) {
        validateWorkoutPayload(incoming, authenticatedUserId);

        Workout toUpdate = new Workout(
                id,
                authenticatedUserId,
                incoming.name(),
                incoming.description(),
                incoming.notes(),
                incoming.duration(),
                null,
                null,
                incoming.sets()
        );

        return repo.updateWorkout(toUpdate)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found"));
    }

    @Transactional(readOnly = true)
    public Workout getWorkoutById(UUID id, UUID authenticatedUserId) {
        return repo.findByIdAndOwnerId(id, authenticatedUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found"));
    }

    @Transactional
    public void deleteWorkoutById(UUID id, UUID authenticatedUserId) {
        boolean deleted = repo.deleteByIdAndOwnerId(id, authenticatedUserId);
        if (!deleted) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workout not found");
        }
    }

    @Transactional(readOnly = true)
    public List<Workout> searchWorkouts(UUID authenticatedUserId, String name, String description, Integer maxDuration) {
        if (maxDuration != null && maxDuration <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "maxDuration must be greater than 0");
        }
        return repo.searchWorkouts(authenticatedUserId, name, description, maxDuration);
    }

    private void validateWorkoutPayload(Workout workout, UUID userId) {
        if (workout == null || workout.name() == null || workout.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Workout name is required");
        }

        if (workout.duration() != null && workout.duration() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duration cannot be negative");
        }

        if (userId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid ownerId is required");
        }
    }
}
