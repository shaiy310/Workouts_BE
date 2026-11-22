package com.YaakoviCorp.Fit1337.repository;

import com.YaakoviCorp.Fit1337.model.ExerciseWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class ExerciseWeightRepositoryImpl implements ExerciseWeightRepository{
    private static final Logger log = LoggerFactory.getLogger(ExerciseWeightRepositoryImpl.class);
    static final String WEIGHT_TABLE_NAME = "user_exercise_weights";

    private final NamedParameterJdbcTemplate jdbc;

    ExerciseWeightRepositoryImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<ExerciseWeight> WEIGHT_ROW_MAPPER = ExerciseWeightRepositoryImpl::mapRow;

    @Override
    public List<ExerciseWeight> findAllByUserId(UUID userId) {
        String sql = """
                SELECT exercise_name, weight, updated_at
                FROM %s
                WHERE user_id = :userId
                ORDER BY LOWER(exercise_name) ASC
                """.formatted(WEIGHT_TABLE_NAME);

        List<ExerciseWeight> weights = jdbc.query(sql, new MapSqlParameterSource("userId", userId), WEIGHT_ROW_MAPPER);
        log.debug("repository=exercise_weights_read outcome=success userId={} resultCount={}", userId, weights.size());
        return weights;
    }

    @Override
    public Optional<ExerciseWeight> findByUserIdAndExerciseName(UUID userId, String exerciseName) {
        String sql = """
                SELECT exercise_name, weight, updated_at
                FROM %s
                WHERE user_id = :userId AND exercise_name = :exerciseName
                """.formatted(WEIGHT_TABLE_NAME);

        List<ExerciseWeight> rows = jdbc.query(
                sql,
                new MapSqlParameterSource()
                        .addValue("userId", userId)
                        .addValue("exerciseName", exerciseName),
                WEIGHT_ROW_MAPPER
        );

        Optional<ExerciseWeight> weight = rows.stream().findFirst();
        log.debug("repository=exercise_weight_read outcome={} userId={} exerciseName={}",
                weight.isPresent() ? "success" : "not_found", userId, exerciseName);
        return weight;
    }

    @Override
    public ExerciseWeight upsertWeight(UUID userId, String exerciseName, double weight) {
        String updateSql = """
                UPDATE %s
                SET weight = :weight,
                    updated_at = NOW()
                WHERE user_id = :userId AND exercise_name = :exerciseName
                """.formatted(WEIGHT_TABLE_NAME);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("exerciseName", exerciseName)
                .addValue("weight", weight);

        int updated = jdbc.update(updateSql, params);
        if (updated == 0) {
            String insertSql = """
                    INSERT INTO %s (user_id, exercise_name, weight)
                    VALUES (:userId, :exerciseName, :weight)
                    """.formatted(WEIGHT_TABLE_NAME);
            jdbc.update(insertSql, params);
        }

        ExerciseWeight saved = findByUserIdAndExerciseName(userId, exerciseName).orElseThrow();
        log.debug("repository=exercise_weight_upsert outcome=success userId={} exerciseName={}", userId, exerciseName);
        return saved;
    }

    @Override
    public boolean deleteByUserIdAndExerciseName(UUID userId, String exerciseName) {
        String sql = """
                DELETE FROM %s
                WHERE user_id = :userId AND exercise_name = :exerciseName
                """.formatted(WEIGHT_TABLE_NAME);

        int deleted = jdbc.update(
                sql,
                new MapSqlParameterSource()
                        .addValue("userId", userId)
                        .addValue("exerciseName", exerciseName)
        );
        boolean wasDeleted = deleted > 0;
        log.debug("repository=exercise_weight_delete outcome={} userId={} exerciseName={}",
                wasDeleted ? "success" : "not_found", userId, exerciseName);
        return wasDeleted;
    }

    private static ExerciseWeight mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ExerciseWeight(
                rs.getString("exercise_name"),
                rs.getDouble("weight"),
                rs.getObject("updated_at", OffsetDateTime.class)
        );
    }
}
