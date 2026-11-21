package com.YaakoviCorp.Fit1337.service;

import com.YaakoviCorp.Fit1337.model.Equipment;
import com.YaakoviCorp.Fit1337.model.Exercise;
import com.YaakoviCorp.Fit1337.model.MuscleGroup;
import com.YaakoviCorp.Fit1337.repository.ExerciseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseService{

    @Autowired
    private ExerciseRepository repo;

    public Exercise createExercise(Exercise exercise) throws JsonProcessingException {
        return repo.createExercise(exercise);
    }

    public Exercise updateExercise(Exercise exercise) throws JsonProcessingException {
        return repo.updateExercise(exercise);
    }

    public Exercise getExercise(String name) throws JsonProcessingException {
        return repo.getExercise(name);
    }

    public void deleteExercise(String name) {

        repo.deleteExercise(name);
    }

    public List<Exercise> getAllExercises() {

        return repo.getAllExercises();
    }

    public List<Exercise> getExercisesByEquipment(Equipment equipment) {
        return repo.getExercisesByEquipment(equipment);
    }

    public List<Exercise> getExercisesByMuscleGroup(MuscleGroup muscleGroup) {
        return repo.getExercisesByMuscleGroup(muscleGroup);
    }
}
