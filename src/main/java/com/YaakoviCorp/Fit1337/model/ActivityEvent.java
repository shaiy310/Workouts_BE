package com.YaakoviCorp.Fit1337.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDate;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = WeightChangeEvent.class, name = "weight"),
        @JsonSubTypes.Type(value = WorkoutEvent.class, name = "workout")
})
public sealed interface ActivityEvent permits WeightChangeEvent, WorkoutEvent {
    UUID id();
    String type();
    LocalDate date();
}

