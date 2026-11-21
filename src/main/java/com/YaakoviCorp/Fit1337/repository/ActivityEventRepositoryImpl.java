package com.YaakoviCorp.Fit1337.repository;

import com.YaakoviCorp.Fit1337.model.ActivityEvent;
import com.YaakoviCorp.Fit1337.model.WeightChangeEvent;
import com.YaakoviCorp.Fit1337.model.WorkoutEvent;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ActivityEventRepositoryImpl implements ActivityEventRepository {

    static final String EVENT_TABLE_NAME = "activity_events";

    private final NamedParameterJdbcTemplate jdbc;

    public ActivityEventRepositoryImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<ActivityEvent> EVENT_ROW_MAPPER = ActivityEventRepositoryImpl::mapRow;

    @Override
    public List<ActivityEvent> findAllByUserId(UUID userId, LocalDate start, LocalDate end) {
        StringBuilder sql = new StringBuilder("""
            SELECT id, event_type, event_date, exercise_name, prev_weight, new_weight, workout_name, workout_description
            FROM %s
            WHERE user_id = :userId
            """.formatted(EVENT_TABLE_NAME));

        MapSqlParameterSource params = new MapSqlParameterSource("userId", userId);

        if (start != null) {
            sql.append(" AND event_date >= :start");
            params.addValue("start", start);
        }

        if (end != null) {
            sql.append(" AND event_date < :end");
            params.addValue("end", end);
        }

        sql.append(" ORDER BY event_date DESC, id DESC");

        return jdbc.query(sql.toString(), params, EVENT_ROW_MAPPER);
    }

    @Override
    public Optional<WorkoutEvent> findWorkoutEventByUserAndDate(UUID userId, LocalDate date) {
        String sql = """
                SELECT id, event_type, event_date, exercise_name, prev_weight, new_weight, workout_name, workout_description
                FROM %s
                WHERE user_id = :userId AND event_type = 'workout' AND event_date = :date
                """.formatted(EVENT_TABLE_NAME);

        List<ActivityEvent> rows = jdbc.query(
                sql,
                new MapSqlParameterSource().addValue("userId", userId).addValue("date", date),
                EVENT_ROW_MAPPER
        );
        return rows.stream().filter(WorkoutEvent.class::isInstance).map(WorkoutEvent.class::cast).findFirst();
    }

    @Override
    public Optional<WeightChangeEvent> findWeightEventByUserExerciseAndDate(UUID userId, String exerciseName, LocalDate date) {
        String sql = """
                SELECT id, event_type, event_date, exercise_name, prev_weight, new_weight, workout_name, workout_description
                FROM %s
                WHERE user_id = :userId
                  AND event_type = 'weight'
                  AND LOWER(exercise_name) = LOWER(:exerciseName)
                  AND event_date = :date
                """.formatted(EVENT_TABLE_NAME);

        List<ActivityEvent> rows = jdbc.query(
                sql,
                new MapSqlParameterSource()
                        .addValue("userId", userId)
                        .addValue("exerciseName", exerciseName)
                        .addValue("date", date),
                EVENT_ROW_MAPPER
        );
        return rows.stream().filter(WeightChangeEvent.class::isInstance).map(WeightChangeEvent.class::cast).findFirst();
    }

    @Override
    public WorkoutEvent saveWorkoutEvent(UUID userId, WorkoutEvent event) {
        UUID id = event.id() != null ? event.id() : UUID.randomUUID();
        String sql = """
                INSERT INTO %s (id, user_id, event_type, event_date, workout_name, workout_description)
                VALUES (:id, :userId, 'workout', :date, :workoutName, :workoutDescription)
                """.formatted(EVENT_TABLE_NAME);

        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userId", userId)
                .addValue("date", event.date())
                .addValue("workoutName", event.workoutName())
                .addValue("workoutDescription", event.workoutDescription()));

        return new WorkoutEvent(id, event.date(), event.workoutName(), event.workoutDescription());
    }

    @Override
    public WorkoutEvent updateWorkoutEvent(UUID userId, WorkoutEvent event) {
        String sql = """
                UPDATE %s
                SET workout_name = :workoutName,
                    workout_description = :workoutDescription
                WHERE id = :id AND user_id = :userId
                """.formatted(EVENT_TABLE_NAME);

        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", event.id())
                .addValue("userId", userId)
                .addValue("workoutName", event.workoutName())
                .addValue("workoutDescription", event.workoutDescription()));

        return event;
    }

    @Override
    public WeightChangeEvent saveWeightEvent(UUID userId, WeightChangeEvent event) {
        UUID id = event.id() != null ? event.id() : UUID.randomUUID();
        String sql = """
                INSERT INTO %s (id, user_id, event_type, event_date, exercise_name, prev_weight, new_weight)
                VALUES (:id, :userId, 'weight', :date, :exerciseName, :prevWeight, :newWeight)
                """.formatted(EVENT_TABLE_NAME);

        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("userId", userId)
                .addValue("date", event.date())
                .addValue("exerciseName", event.exerciseName())
                .addValue("prevWeight", event.prevWeight())
                .addValue("newWeight", event.newWeight()));

        return new WeightChangeEvent(id, event.date(), event.exerciseName(), event.prevWeight(), event.newWeight());
    }

    @Override
    public WeightChangeEvent updateWeightEvent(UUID userId, WeightChangeEvent event) {
        String sql = """
                UPDATE %s
                SET prev_weight = :prevWeight,
                    new_weight = :newWeight
                WHERE id = :id AND user_id = :userId
                """.formatted(EVENT_TABLE_NAME);

        jdbc.update(sql, new MapSqlParameterSource()
                .addValue("id", event.id())
                .addValue("userId", userId)
                .addValue("prevWeight", event.prevWeight())
                .addValue("newWeight", event.newWeight()));

        return event;
    }

    @Override
    public void deleteByIdAndUserId(UUID id, UUID userId) {
        String sql = "DELETE FROM %s WHERE id = :id AND user_id = :userId".formatted(EVENT_TABLE_NAME);
        jdbc.update(sql, new MapSqlParameterSource().addValue("id", id).addValue("userId", userId));
    }

    private static ActivityEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = rs.getObject("id", UUID.class);
        String type = rs.getString("event_type");
        LocalDate date = rs.getObject("event_date", LocalDate.class);

        return switch (type) {
            case "weight" -> new WeightChangeEvent(
                    id,
                    date,
                    rs.getString("exercise_name"),
                    rs.getDouble("prev_weight"),
                    rs.getDouble("new_weight")
            );
            case "workout" -> new WorkoutEvent(
                    id,
                    date,
                    rs.getString("workout_name"),
                    rs.getString("workout_description")
            );
            default -> throw new IllegalStateException("Unknown event_type: " + type);
        };
    }
}
