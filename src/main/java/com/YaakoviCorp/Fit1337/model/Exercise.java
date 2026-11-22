package com.YaakoviCorp.Fit1337.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Exercise {
    @JsonProperty("name")
    String name;

    @JsonProperty("muscle_group")
    MuscleGroup muscleGroup;

    @JsonProperty("equipment")
    Equipment equipment;

    double weight;

    String imagePath;

    public Exercise(String name, MuscleGroup muscleGroup, Equipment equipment, double weight, String imagePath) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
        this.weight = weight;
        this.imagePath = imagePath;
    }

    public String getName() {
        return this.name;
    }
    public MuscleGroup getMuscleGroup() {
        return this.muscleGroup;
    }
    public Equipment getEquipment() {
        return this.equipment;
    }
    public double getWeight() {
        return this.weight;
    }
    public String getImagePath() {
        return this.imagePath;
    }

}

