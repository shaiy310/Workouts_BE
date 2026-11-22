package com.YaakoviCorp.Fit1337.controller;

import com.YaakoviCorp.Fit1337.model.Workout;
import com.YaakoviCorp.Fit1337.security.model.AuthenticatedUser;
import com.YaakoviCorp.Fit1337.service.WorkoutService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {
    private static final Logger log = LoggerFactory.getLogger(WorkoutController.class);
    final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    // POST /api/workouts
    @PostMapping
    public ResponseEntity<Workout> createWorkout(
            @RequestBody @Valid Workout workout,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        Workout created = workoutService.createWorkout(workout, currentUser.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/workouts/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Workout> updateWorkout(
            @PathVariable UUID id,
            @RequestBody @Valid Workout workout,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        Workout updated = workoutService.updateWorkout(id, workout, currentUser.id());
        return ResponseEntity.ok(updated);
    }

    // GET /api/workouts/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Workout> getWorkout(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(workoutService.getWorkoutById(id, currentUser.id()));    }

    // DELETE /api/workouts/{name}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkout(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        workoutService.deleteWorkoutById(id, currentUser.id());
        return ResponseEntity.noContent().build();
    }

    // GET /api/workouts
    // GET /api/workouts?name=push&description=hypertrophy&maxDuration=45
    @GetMapping
    public ResponseEntity<List<Workout>> getWorkouts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer maxDuration,
            @AuthenticationPrincipal AuthenticatedUser currentUser) {
        List<Workout> workouts = workoutService.searchWorkouts(
                currentUser.id(),
                name,
                description,
                maxDuration
        );
        return ResponseEntity.ok(workouts);
    }
}
