package com.YaakoviCorp.Fit1337.service;

import com.YaakoviCorp.Fit1337.model.Equipment;
import com.YaakoviCorp.Fit1337.model.Exercise;
import com.YaakoviCorp.Fit1337.model.MuscleGroup;
import com.YaakoviCorp.Fit1337.repository.ExerciseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService{
    private static final Logger log = LoggerFactory.getLogger(ExerciseService.class);

    private final ExerciseRepository repo;

    ExerciseService(ExerciseRepository repo) {
        this.repo = repo;
    }

    public Exercise createExercise(Exercise exercise) throws JsonProcessingException {
        Exercise created = repo.createExercise(exercise);
        log.info("service=exercise_create outcome=success exerciseName={}", created.getName());
        return created;
    }

    public Exercise updateExercise(Exercise exercise) throws JsonProcessingException {
        Exercise updated = repo.updateExercise(exercise);
        log.info("service=exercise_update outcome=success exerciseName={}", updated.getName());
        return updated;
    }

    public Exercise getExercise(String name) throws JsonProcessingException {
        Exercise exercise = repo.getExercise(name);
        log.debug("service=exercise_read outcome=success exerciseName={}", name);
        return exercise;
    }

    public void deleteExercise(String name) {
        repo.deleteExercise(name);
        log.info("service=exercise_delete outcome=success exerciseName={}", name);
    }

    public List<Exercise> getAllExercises() {

        List<Exercise> exercises = repo.getAllExercises();
        log.debug("service=exercise_list outcome=success resultCount={}", exercises.size());
        return exercises;
    }

    public List<Exercise> getExercisesByEquipment(Equipment equipment) {
        List<Exercise> exercises = repo.getExercisesByEquipment(equipment);
        log.debug("service=exercise_list outcome=success filter=equipment equipment={} resultCount={}", equipment, exercises.size());
        return exercises;
    }

    public List<Exercise> getExercisesByMuscleGroup(MuscleGroup muscleGroup) {
        List<Exercise> exercises = repo.getExercisesByMuscleGroup(muscleGroup);
        log.debug("service=exercise_list outcome=success filter=muscle_group muscleGroup={} resultCount={}", muscleGroup, exercises.size());
        return exercises;
    }
}
