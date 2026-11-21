package com.YaakoviCorp.Fit1337.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;

public class Exercise {
    @JsonProperty("name")
    String name;

    @JsonProperty("muscle_group")
    MuscleGroup muscleGroup;

    Equipment equipment;

    float weight;

    String imagePath;

    public Exercise(String name, MuscleGroup muscleGroup, Equipment equipment, float weight, String imagePath) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
        this.weight = weight;
        this.imagePath = imagePath;
    }

    public String getName() {
        return this.name;
    }
    public @Nullable Object getMuscleGroup() {
        return this.muscleGroup;
    }
    public Equipment getEquipment() {
        return this.equipment;
    }
    public float getWeight() {
        return this.weight;
    }
    public String getImagePath() {
        return this.imagePath;
    }

}

