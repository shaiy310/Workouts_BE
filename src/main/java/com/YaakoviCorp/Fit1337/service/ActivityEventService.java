package com.YaakoviCorp.Fit1337.service;

import com.YaakoviCorp.Fit1337.model.ActivityEvent;
import com.YaakoviCorp.Fit1337.model.WeightChangeEvent;
import com.YaakoviCorp.Fit1337.model.WorkoutEvent;
import com.YaakoviCorp.Fit1337.repository.ActivityEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ActivityEventService {
    private static final Logger log = LoggerFactory.getLogger(ActivityEventService.class);

    private final ActivityEventRepository repo;

    public ActivityEventService(ActivityEventRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<ActivityEvent> getUserEvents(UUID userId, LocalDate start, LocalDate end) {
        if (start != null && end != null && !start.isBefore(end)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "'start' must be before 'end' (end is exclusive)"
            );
        }
        List<ActivityEvent> events = repo.findAllByUserId(userId, start, end);
        log.debug("service=activity_events_read outcome=success userId={} resultCount={}", userId, events.size());
        return events;
    }

    @Transactional
    public Optional<ActivityEvent> recordEvent(ActivityEvent incoming, UUID userId) {
        if (incoming == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event payload is required");
        }

        LocalDate eventDate = incoming.date() != null ? incoming.date() : LocalDate.now();

        Optional<ActivityEvent> recorded = switch (incoming) {
            case WorkoutEvent w -> Optional.of(handleWorkoutEvent(w, eventDate, userId));
            case WeightChangeEvent wc -> handleWeightEvent(wc, eventDate, userId);
        };
        log.info("service=activity_event_record outcome=success userId={} eventDate={} eventType={} recorded={}",
                userId, eventDate, incoming.getClass().getSimpleName(), recorded.isPresent());
        return recorded;
    }

    private WorkoutEvent handleWorkoutEvent(WorkoutEvent incoming, LocalDate eventDate, UUID userId) {
        if (incoming.workoutName() == null || incoming.workoutName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Workout name is required");
        }

        if (repo.findWorkoutEventByUserAndDate(userId, eventDate).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A workout event already exists for date " + eventDate
            );
        }

        WorkoutEvent toSave = new WorkoutEvent(
                null,
                eventDate,
                incoming.workoutName().trim(),
                incoming.workoutDescription() != null ? incoming.workoutDescription().trim() : null
        );
        return repo.saveWorkoutEvent(userId, toSave);
    }

    private Optional<ActivityEvent> handleWeightEvent(WeightChangeEvent incoming, LocalDate eventDate, UUID userId) {
        if (incoming.exerciseName() == null || incoming.exerciseName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Exercise name is required");
        }
        validateWeightRange(incoming.prevWeight(), "prevWeight");
        validateWeightRange(incoming.newWeight(), "newWeight");

        String exerciseName = incoming.exerciseName().trim();
        double mergedPrevValue;
        double mergedNewValue = incoming.newWeight();

        Optional<WeightChangeEvent> existingOpt =
                repo.findWeightEventByUserExerciseAndDate(userId, exerciseName, eventDate);
        if (existingOpt.isPresent()) {
            WeightChangeEvent existing = existingOpt.get();
            // Earliest prevWeight of the day stays first; incoming newWeight is latest of the day
            mergedPrevValue = existing.prevWeight();

            if (Double.compare(mergedPrevValue, mergedNewValue) == 0) {
                repo.deleteByIdAndUserId(existing.id(), userId);
                return Optional.empty();
            }

            WeightChangeEvent updated = new WeightChangeEvent(
                    existing.id(),
                    eventDate,
                    existing.exerciseName(),
                    mergedPrevValue,
                    mergedNewValue
            );
            return Optional.of(repo.updateWeightEvent(userId, updated));
        }

        mergedPrevValue = incoming.prevWeight();
        if (Double.compare(mergedPrevValue, mergedNewValue) == 0) {
            return Optional.empty();
        }

        WeightChangeEvent toSave = new WeightChangeEvent(
                null,
                eventDate,
                exerciseName,
                mergedPrevValue,
                mergedNewValue
        );
        return Optional.of(repo.saveWeightEvent(userId, toSave));
    }

    private void validateWeightRange(double value, String fieldName) {
        if (Double.isNaN(value) || value < 0 || value > 40) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    fieldName + " must be between 0 and 40"
            );
        }
    }
}