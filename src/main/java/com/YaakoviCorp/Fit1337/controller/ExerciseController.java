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
        return exerciseService.createExercise(exercise);
    }

    @PutMapping()
    public Exercise updateExercise(@RequestBody Exercise exercise) throws JsonProcessingException {
        return exerciseService.updateExercise(exercise);
    }

    @GetMapping("/{name}")
    public Exercise getExercise(@PathVariable String name) throws JsonProcessingException {
        log.debug("getting exercise: '{}'", name);
        return exerciseService.getExercise(name);
    }

    @DeleteMapping("/{name}")
    public void deleteExercise(@PathVariable String name) {
        log.debug("deleting exercise: '{}'", name);
        exerciseService.deleteExercise(name);
    }

    @GetMapping()
    public List<Exercise> getAllExercises() {
        log.debug("getting all exercises");
        return exerciseService.getAllExercises();
    }

    @GetMapping("/equipment")
    public List<Exercise> getAllExercisesByEquipment(@RequestParam Equipment e) {
        log.debug("getting all exercises with equipment '{}'", e);
        return exerciseService.getExercisesByEquipment(e);
    }

    @GetMapping("/muscleGroup")
    public List<Exercise> getAllExercisesByMuscleGroup(@RequestParam MuscleGroup mg) {
        log.debug("getting all exercises with muscle group '{}'", mg);
        return exerciseService.getExercisesByMuscleGroup(mg);
    }
}
