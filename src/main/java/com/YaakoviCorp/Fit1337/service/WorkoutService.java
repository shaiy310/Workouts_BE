package com.YaakoviCorp.Fit1337.service;

import com.YaakoviCorp.Fit1337.model.Workout;
import com.YaakoviCorp.Fit1337.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class WorkoutService{
    @Autowired
    private WorkoutRepository repo;

    @Transactional
    public Workout createWorkout(Workout workout, UUID authenticatedUserId) {
        validateWorkout(workout);
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

        return repo.createWorkout(ownedWorkout);
    }

    @Transactional
    public Workout updateWorkout(UUID id, Workout incoming, UUID authenticatedUserId) {
        validateWorkout(incoming);

        // TODO: validate permissions of authenticatedUserId

        UUID userId = incoming.ownerId() != null ? incoming.ownerId() : authenticatedUserId;
        Workout toUpdate = new Workout(
                id,
                userId,
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

    private void validateWorkout(Workout workout) {
        if (workout == null || workout.name() == null || workout.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Workout name is required");
        }

        if (workout.duration() != null && workout.duration() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duration cannot be negative");
        }

        if (workout.ownerId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid ownerId is required");
        }
    }
}
