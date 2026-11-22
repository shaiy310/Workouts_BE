package com.YaakoviCorp.Fit1337.controller;

import com.YaakoviCorp.Fit1337.model.Equipment;
import com.YaakoviCorp.Fit1337.model.Exercise;
import com.YaakoviCorp.Fit1337.model.MuscleGroup;
import com.YaakoviCorp.Fit1337.service.ExerciseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/public/exercises")
public class ExerciseController {
    private static final Logger log = LoggerFactory.getLogger(ExerciseController.class);

    private final ExerciseService exerciseService;

    ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping()
    public Exercise addExercise(@RequestBody Exercise exercise) throws JsonProcessingException {
        Exercise created = exerciseService.createExercise(exercise);
        log.info("controller=exercise_create outcome=success exerciseName={}", created.getName());
        return created;
    }

    @PutMapping()
    public Exercise updateExercise(@RequestBody Exercise exercise) throws JsonProcessingException {
        Exercise updated = exerciseService.updateExercise(exercise);
        log.info("controller=exercise_update outcome=success exerciseName={}", updated.getName());
        return updated;
    }

    @GetMapping("/{name}")
    public Exercise getExercise(@PathVariable String name) throws JsonProcessingException {
        Exercise exercise = exerciseService.getExercise(name);
        log.debug("controller=exercise_read outcome=success exerciseName={}", name);
        return exercise;
    }

    @DeleteMapping("/{name}")
    public void deleteExercise(@PathVariable String name) {
        exerciseService.deleteExercise(name);
        log.info("controller=exercise_delete outcome=success exerciseName={}", name);
    }

    @GetMapping()
    public List<Exercise> getAllExercises() {
        List<Exercise> exercises = exerciseService.getAllExercises();
        log.debug("controller=exercise_list outcome=success resultCount={}", exercises.size());
        return exercises;
    }

    @GetMapping("/equipment")
    public List<Exercise> getAllExercisesByEquipment(@RequestParam Equipment e) {
        List<Exercise> exercises = exerciseService.getExercisesByEquipment(e);
        log.debug("controller=exercise_list outcome=success filter=equipment equipment={} resultCount={}", e, exercises.size());
        return exercises;
    }

    @GetMapping("/muscleGroup")
    public List<Exercise> getAllExercisesByMuscleGroup(@RequestParam MuscleGroup mg) {
        List<Exercise> exercises = exerciseService.getExercisesByMuscleGroup(mg);
        log.debug("controller=exercise_list outcome=success filter=muscle_group muscleGroup={} resultCount={}",
                mg, exercises.size());
        return exercises;
    }
}
