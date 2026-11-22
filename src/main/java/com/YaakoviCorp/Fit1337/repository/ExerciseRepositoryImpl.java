package com.YaakoviCorp.Fit1337.repository;

import com.YaakoviCorp.Fit1337.model.Equipment;
import com.YaakoviCorp.Fit1337.model.Exercise;
import com.YaakoviCorp.Fit1337.model.MuscleGroup;
import com.YaakoviCorp.Fit1337.repository.redis.CacheRepository;
import com.YaakoviCorp.Fit1337.repository.mapper.ExerciseMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExerciseRepositoryImpl  implements ExerciseRepository {
    static final String EXERCISE_TABLE_NAME = "exercises";

    private static final Logger log = LoggerFactory.getLogger(ExerciseRepositoryImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    CacheRepository cacheRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public Exercise createExercise(Exercise exercise) throws JsonProcessingException {
        String sql = String.format("INSERT INTO %s (name, muscle_group, equipment, weight, imagePath) VALUES (?, ?, ?, ?, ?)", EXERCISE_TABLE_NAME);
        jdbcTemplate.update(sql,
                exercise.getName(),
                exercise.getMuscleGroup().name(),
                exercise.getEquipment().name(),
                exercise.getWeight(),
                exercise.getImagePath()
        );

        Exercise created = getExerciseFromDB(exercise.getName());
        log.debug("repository=exercise_create outcome=success exerciseName={}", created.getName());
        return created;
    }

    @Override
    public Exercise updateExercise(Exercise exercise) throws JsonProcessingException {
        String sql = String.format("UPDATE %s SET (muscle_group=?, equipment=?, weight=?, imagePath=?) WHERE name=?", EXERCISE_TABLE_NAME);
        jdbcTemplate.update(sql,
                exercise.getMuscleGroup().name(),
                exercise.getEquipment().name(),
                exercise.getWeight(),
                exercise.getImagePath(),
                exercise.getName()
        );

        Exercise updated = getExerciseFromDB(exercise.getName());
        log.debug("repository=exercise_update outcome=success exerciseName={}", updated.getName());
        return updated;
    }

    @Override
    public Exercise getExercise(String name) throws JsonProcessingException {
        String cacheExercise = cacheRepository.getCacheEntity(name);
        if  (cacheExercise != null) {
            Exercise exercise = objectMapper.readValue(cacheExercise, Exercise.class);
            log.debug("repository=exercise_read outcome=success source=cache exerciseName={}", name);
            return exercise;
        }

        Exercise exercise = getExerciseFromDB(name);
        log.debug("repository=exercise_read outcome=success source=db exerciseName={}", name);
        return exercise;
    }

    @Override
    public void deleteExercise(String name) {
        String sql = String.format("DELETE FROM %s WHERE name=?", EXERCISE_TABLE_NAME);
        jdbcTemplate.update(sql, name);
        cacheRepository.removeCacheEntity(name);
        log.debug("repository=exercise_delete outcome=success exerciseName={}", name);
    }

    @Override
    public List<Exercise> getAllExercises() {
        String sql = String.format("SELECT * FROM %s", EXERCISE_TABLE_NAME);
        try {
            List<Exercise> exercises = jdbcTemplate.query(sql, new ExerciseMapper());
            log.debug("repository=exercise_list outcome=success resultCount={}", exercises.size());
            return exercises;

        } catch (EmptyResultDataAccessException e) {
            log.error("repository=exercise_list outcome=failed reason=no_exercises");
            return null;
        }
    }

    @Override
    public List<Exercise> getExercisesByEquipment(Equipment equipment) {
        String sql = String.format("SELECT * FROM %s WHERE equipment=?", EXERCISE_TABLE_NAME);
        try {
            List<Exercise> exercises = jdbcTemplate.query(sql, new ExerciseMapper(), equipment.name());
            log.debug("repository=exercise_list outcome=success filter=equipment equipment={} resultCount={}", equipment, exercises.size());
            return exercises;

        } catch (EmptyResultDataAccessException e) {
            log.error("repository=exercise_list outcome=failed reason=no_exercises filter=equipment equipment={}", equipment);
            return null;
        }
    }

    @Override
    public List<Exercise> getExercisesByMuscleGroup(MuscleGroup muscleGroup) {
        String sql = String.format("SELECT * FROM %s WHERE muscle_group=?", EXERCISE_TABLE_NAME);
        List<Exercise> exercises = jdbcTemplate.query(sql, new ExerciseMapper(), muscleGroup.name());
        log.debug("repository=exercise_list outcome=success filter=muscle_group muscleGroup={} resultCount={}",
                muscleGroup, exercises.size());
        return exercises;
    }

    private Exercise getExerciseFromDB(String name) throws JsonProcessingException {
        String sql = String.format("SELECT * FROM %s WHERE name=?", EXERCISE_TABLE_NAME);
        Exercise exercise = jdbcTemplate.queryForObject(sql, new ExerciseMapper(), name);
        String exerciseString = objectMapper.writeValueAsString(exercise);
        assert exercise != null;
        cacheRepository.createCacheEntity(exercise.getName(), exerciseString);

        return exercise;
    }


}
