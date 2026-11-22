package com.YaakoviCorp.Fit1337.repository;

import com.YaakoviCorp.Fit1337.model.ExerciseSet;
import com.YaakoviCorp.Fit1337.model.Workout;
import com.YaakoviCorp.Fit1337.model.WorkoutSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.*;

@Repository
public class WorkoutRepositoryImpl implements WorkoutRepository{
    private static final Logger log = LoggerFactory.getLogger(WorkoutRepositoryImpl.class);
    static final String WORKOUT_TABLE_NAME = "workouts";
    static final String WORKOUT_SET_TABLE_NAME = "workout_sets";
    static final String SET_EXERCISE_TABLE_NAME = "workout_set_exercises";

    private final NamedParameterJdbcTemplate jdbc;

    public WorkoutRepositoryImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String BASE_SELECT = """
            SELECT w.id AS w_id,
                   w.owner_id AS w_owner_id,
                   w.name AS w_name,
                   w.description AS w_description,
                   w.notes AS w_notes,
                   w.duration AS w_duration,
                   w.created_at AS w_created_at,
                   w.updated_at AS w_updated_at,
                   ws.id AS ws_id,
                   ws.set_number AS ws_set_number,
                   ws.rest AS ws_rest,
                   wse.id AS wse_id,
                   wse.exercise_name AS wse_exercise_name,
                   wse.reps AS wse_reps
            FROM %s w
            LEFT JOIN %s ws ON ws.workout_id = w.id
            LEFT JOIN %s wse ON wse.workout_set_id = ws.id
            """.formatted(WORKOUT_TABLE_NAME, WORKOUT_SET_TABLE_NAME, SET_EXERCISE_TABLE_NAME);

    private static final ResultSetExtractor<List<Workout>> WORKOUT_LIST_EXTRACTOR = WorkoutRepositoryImpl::extractWorkouts;

    @Override
    @Transactional
    public Workout createWorkout(Workout workout) {
        UUID workoutId = UUID.randomUUID();

        String sql = """
            INSERT INTO %s (id, name, duration, description, notes, owner_id)
            VALUES (:id, :name, :duration, :description, :notes, :ownerId)
            """.formatted(WORKOUT_TABLE_NAME);

        jdbc.update(
                sql,
                new MapSqlParameterSource()
                        .addValue("id", workoutId)
                        .addValue("name", workout.name().trim())
                        .addValue("duration", workout.duration())
                        .addValue("description", trimOrNull(workout.description()))
                        .addValue("notes", trimOrNull(workout.notes()))
                        .addValue("ownerId", workout.ownerId())
        );

        insertSetsAndExercises(workoutId, workout.sets());
        Workout created = findByIdAndOwnerId(workoutId, workout.ownerId()).orElseThrow();
        log.debug("repository=workout_create outcome=success workoutId={} userId={}", workoutId, workout.ownerId());
        return created;
    }

    @Override
    @Transactional
    public Optional<Workout> updateWorkout(Workout workout) {
        if (workout.id() == null) {
            throw new IllegalArgumentException("Workout ID must not be null when updating");
        }

        String updateWorkoutSql = """
                UPDATE %s
                SET name = :name,
                    duration = :duration,
                    description = :description,
                    notes = :notes,
                    updated_at = NOW()
                WHERE id = :id AND owner_id = :ownerId
                """.formatted(WORKOUT_TABLE_NAME);

        int updatedRows = jdbc.update(
                updateWorkoutSql,
                new MapSqlParameterSource()
                        .addValue("id", workout.id())
                        .addValue("ownerId", workout.ownerId())
                        .addValue("name", workout.name().trim())
                        .addValue("duration", workout.duration())
                        .addValue("description", trimOrNull(workout.description()))
                        .addValue("notes", trimOrNull(workout.notes()))
        );
        if (updatedRows == 0) {
            log.debug("repository=workout_update outcome=not_found workoutId={} userId={}", workout.id(), workout.ownerId());
            return Optional.empty();
        }

        // Cascades to workout_set_exercises via ON DELETE CASCADE
        jdbc.update(
                "DELETE FROM %s WHERE workout_id = :workoutId".formatted(WORKOUT_SET_TABLE_NAME),
                new MapSqlParameterSource("workoutId", workout.id())
        );

        insertSetsAndExercises(workout.id(), workout.sets());
        Optional<Workout> updated = findByIdAndOwnerId(workout.id(), workout.ownerId());
        log.debug("repository=workout_update outcome=success workoutId={} userId={}", workout.id(), workout.ownerId());
        return updated;
    }

    @Override
    public Optional<Workout> findByIdAndOwnerId(UUID id, UUID ownerId) {
        String sql = BASE_SELECT + """
                WHERE w.id = :id AND w.owner_id = :ownerId
                ORDER BY ws.set_order ASC, wse.exercise_order ASC
                """;

        List<Workout> results = jdbc.query(
                sql,
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("ownerId", ownerId),
                WORKOUT_LIST_EXTRACTOR
        );

        Optional<Workout> workout = (results == null || results.isEmpty()) ? Optional.empty() : Optional.of(results.getFirst());
        log.debug("repository=workout_read outcome={} workoutId={} userId={}",
                workout.isPresent() ? "success" : "not_found", id, ownerId);
        return workout;
    }

    @Override
    public boolean deleteByIdAndOwnerId(UUID id, UUID ownerId) {
        String sql = "DELETE FROM %s WHERE id = :id AND owner_id = :ownerId".formatted(WORKOUT_TABLE_NAME);
        int deleted = jdbc.update(
                sql,
                new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("ownerId", ownerId)
        );
        boolean wasDeleted = deleted > 0;
        log.debug("repository=workout_delete outcome={} workoutId={} userId={}",
                wasDeleted ? "success" : "not_found", id, ownerId);
        return wasDeleted;
    }

    @Override
    public List<Workout> searchWorkouts(UUID ownerId, String name, String description, Integer maxDuration) {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE w.owner_id = :ownerId");
        MapSqlParameterSource params = new MapSqlParameterSource("ownerId", ownerId);

        if (name != null && !name.isBlank()) {
            sql.append(" AND LOWER(w.name) LIKE LOWER(:name)");
            params.addValue("name", "%" + name.trim() + "%");
        }

        if (description != null && !description.isBlank()) {
            sql.append(" AND LOWER(w.description) = LOWER(:description)");
            params.addValue("description", description.trim());
        }

        if (maxDuration != null) {
            sql.append(" AND w.duration <= :maxDuration");
            params.addValue("maxDuration", maxDuration);
        }

        sql.append(" ORDER BY w.name DESC, ws.set_order ASC, wse.exercise_order ASC");

        List<Workout> results = jdbc.query(sql.toString(), params, WORKOUT_LIST_EXTRACTOR);
        log.debug("repository=workout_search outcome=success userId={} resultCount={}",
                ownerId, results == null ? 0 : results.size());
        return results != null ? results : List.of();
    }

    // --- Helper Methods ---

    private void insertSetsAndExercises(UUID workoutId, List<WorkoutSet> sets) {
        if (sets == null || sets.isEmpty()) {
            return;
        }

        String insertSetSql = """
            INSERT INTO %s (id, workout_id, set_order, set_number, rest)
            VALUES (:id, :workoutId, :setOrder, :setNumber, :rest)
            """.formatted(WORKOUT_SET_TABLE_NAME);

        String insertExerciseSql = """
            INSERT INTO %s (id, workout_set_id, exercise_order, exercise_name, reps)
            VALUES (:id, :workoutSetId, :exerciseOrder, :exerciseName, :reps)
            """.formatted(SET_EXERCISE_TABLE_NAME);

        List<MapSqlParameterSource> setBatch = new ArrayList<>(sets.size());
        List<MapSqlParameterSource> exerciseBatch = new ArrayList<>();

        for (int setIndex = 0; setIndex < sets.size(); setIndex++) {
            WorkoutSet set = sets.get(setIndex);
            UUID setId = UUID.randomUUID();
            int setOrder = setIndex + 1;
            int setNumber = set.setNumber() > 0 ? set.setNumber() : 3;

            setBatch.add(new MapSqlParameterSource()
                    .addValue("id", setId)
                    .addValue("workoutId", workoutId)
                    .addValue("setOrder", setOrder)
                    .addValue("setNumber", setNumber)
                    .addValue("rest", set.rest())
            );

            List<ExerciseSet> exercises = set.exercises();
            if (exercises == null || exercises.isEmpty()) continue;

            for (int exIndex = 0; exIndex < exercises.size(); exIndex++) {
                ExerciseSet ex = exercises.get(exIndex);
                exerciseBatch.add(new MapSqlParameterSource()
                        .addValue("id", UUID.randomUUID())
                        .addValue("workoutSetId", setId)
                        .addValue("exerciseOrder", exIndex + 1)
                        .addValue("exerciseName", ex.name())
                        .addValue("reps", ex.reps())
                );
            }
        }

        jdbc.batchUpdate(insertSetSql, setBatch.toArray(MapSqlParameterSource[]::new));

        if (!exerciseBatch.isEmpty()) {
            jdbc.batchUpdate(insertExerciseSql, exerciseBatch.toArray(MapSqlParameterSource[]::new));
        }
    }

    // --- ResultSetExtractor to assemble nested immutable records ---

    private static List<Workout> extractWorkouts(ResultSet rs) throws SQLException {
        Map<UUID, WorkoutBuilder> workoutsById = new LinkedHashMap<>();

        while (rs.next()) {
            UUID workoutId = rs.getObject("w_id", UUID.class);
            WorkoutBuilder wb = workoutsById.computeIfAbsent(workoutId, id -> {
                try {
                    return new WorkoutBuilder(
                            id,
                            rs.getObject("w_owner_id", UUID.class),
                            rs.getString("w_name"),
                            rs.getString("w_description"),
                            rs.getString("w_notes"),
                            rs.getObject("w_duration", Integer.class),
                            rs.getObject("w_created_at", OffsetDateTime.class),
                            rs.getObject("w_updated_at", OffsetDateTime.class)
                    );
                } catch (SQLException exc) {
                    throw new RuntimeException(exc);
                }
            });

            UUID setId = rs.getObject("ws_id", UUID.class);
            if (setId != null) {
                WorkoutSetBuilder sb = wb.sets.computeIfAbsent(setId, id -> {
                    try {
                        return new WorkoutSetBuilder(
                                rs.getInt("ws_set_number"),
                                rs.getInt("ws_rest")
                        );
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                UUID exerciseId = rs.getObject("wse_id", UUID.class);
                if (exerciseId != null) {
                    try {
                        sb.exercises.add(new ExerciseSet(
                                rs.getString("wse_exercise_name"),
                                rs.getInt("wse_reps")
                        ));
                    } catch (SQLException exc) {
                        throw new RuntimeException(exc);
                    }
                }
            }
        }

        return workoutsById.values().stream()
                .map(WorkoutBuilder::build)
                .toList();
    }

    private static class WorkoutBuilder {
        final UUID id;
        final UUID ownerId;
        final String name;
        final String description;
        final String notes;
        final Integer duration;
        final OffsetDateTime createdAt;
        final OffsetDateTime updatedAt;
        final Map<UUID, WorkoutSetBuilder> sets = new LinkedHashMap<>();

        WorkoutBuilder(UUID id, UUID ownerId, String name, String description, String notes,
                       Integer duration, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
            this.id = id;
            this.ownerId = ownerId;
            this.name = name;
            this.description = description;
            this.notes = notes;
            this.duration = duration;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        Workout build() {
            List<WorkoutSet> builtSets = sets.values().stream()
                    .map(WorkoutSetBuilder::build)
                    .toList();
            return new Workout(id, ownerId, name, description, notes, duration, createdAt, updatedAt, builtSets);
        }
    }

    private static class WorkoutSetBuilder {
        final int setNumber;
        final int rest;
        final List<ExerciseSet> exercises = new ArrayList<>();

        WorkoutSetBuilder(int setNumber, int rest) {
            this.setNumber = setNumber;
            this.rest = rest;
        }

        WorkoutSet build() {
            return new WorkoutSet(setNumber, rest, exercises);
        }
    }

    private static String trimOrNull(String value) {
        return value == null ? null : value.trim();
    }
}
