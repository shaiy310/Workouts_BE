package com.YaakoviCorp.Fit1337.controller;

import com.YaakoviCorp.Fit1337.model.Equipment;
import com.YaakoviCorp.Fit1337.model.Exercise;
import com.YaakoviCorp.Fit1337.model.MuscleGroup;
import com.YaakoviCorp.Fit1337.service.ExerciseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/public/exercises")
public class ExerciseController {
    private final Logger logger = LogManager.getLogger(ExerciseController.class);

    @Autowired
    private ExerciseService exerciseService;

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
        logger.debug("getting exercise: '{}'", name);
        return exerciseService.getExercise(name);
    }

    @DeleteMapping("/{name}")
    public void deleteExercise(@PathVariable String name) {
        logger.debug("deleting exercise: '{}'", name);
        exerciseService.deleteExercise(name);
    }

    @GetMapping()
    public List<Exercise> getAllExercises() {
        logger.debug("getting all exercises");
        return exerciseService.getAllExercises();
    }

    @GetMapping("/equipment")
    public List<Exercise> getAllExercisesByEquipment(@RequestParam Equipment e) {
        logger.debug("getting all exercises with equipment '{}'", e);
        return exerciseService.getExercisesByEquipment(e);
    }

    @GetMapping("/muscleGroup")
    public List<Exercise> getAllExercisesByMuscleGroup(@RequestParam MuscleGroup mg) {
        logger.debug("getting all exercises with muscle group '{}'", mg);
        return exerciseService.getExercisesByMuscleGroup(mg);
    }
}
