package com.YaakoviCorp.Fit1337.repository;

import com.YaakoviCorp.Fit1337.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ExerciseRepository {
    Exercise createExercise(Exercise exercise) throws JsonProcessingException;
    Exercise updateExercise(Exercise exercise) throws JsonProcessingException;
    Exercise getExercise(String name) throws JsonProcessingException;
    void deleteExercise(String name);
    List<Exercise> getAllExercises();
    List<Exercise> getExercisesByEquipment(Equipment equipment);
    List<Exercise> getExercisesByMuscleGroup(MuscleGroup muscleGroup);
}

