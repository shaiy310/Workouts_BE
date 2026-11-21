package com.YaakoviCorp.Fit1337.repository;

import com.YaakoviCorp.Fit1337.model.Equipment;
import com.YaakoviCorp.Fit1337.model.Exercise;
import com.YaakoviCorp.Fit1337.model.MuscleGroup;
import com.YaakoviCorp.Fit1337.repository.redis.CacheRepository;
import com.YaakoviCorp.Fit1337.repository.mapper.ExerciseMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExerciseRepositoryImpl  implements ExerciseRepository {
    static final String EXERCISE_TABLE_NAME = "exercises";

    private final Logger logger = LogManager.getLogger(ExerciseRepositoryImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    CacheRepository cacheRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public Exercise createExercise(Exercise exercise) throws JsonProcessingException {
        String sql = String.format("INSERT INTO %s (name, muscleGroup, equipment, weight, imagePath) VALUES (?, ?, ?, ?)", EXERCISE_TABLE_NAME);
        jdbcTemplate.update(sql,
                exercise.getName(),
                exercise.getMuscleGroup(),
                exercise.getEquipment(),
                exercise.getWeight(),
                exercise.getImagePath()
        );

        return getExerciseFromDB(exercise.getName());
    }

    @Override
    public Exercise updateExercise(Exercise exercise) throws JsonProcessingException {
        String sql = String.format("UPDATE %s SET equipment=?, weight=?, imagePath=?) WHERE name=?", EXERCISE_TABLE_NAME);
        jdbcTemplate.update(sql,
                exercise.getEquipment(),
                exercise.getWeight(),
                exercise.getImagePath(),
                exercise.getName()
        );

        return getExerciseFromDB(exercise.getName());
    }

    @Override
    public Exercise getExercise(String name) throws JsonProcessingException {
        String cacheExercise = cacheRepository.getCacheEntity(name);
        if  (cacheExercise != null) {
            return objectMapper.readValue(cacheExercise, Exercise.class);
        }

        return getExerciseFromDB(name);
    }

    @Override
    public void deleteExercise(String name) {
        String sql = String.format("DELETE FROM %s WHERE name=?", EXERCISE_TABLE_NAME);
        jdbcTemplate.update(sql, name);
        cacheRepository.removeCacheEntity(name);
    }

    @Override
    public List<Exercise> getAllExercises() {
        String sql = String.format("SELECT * FROM %s", EXERCISE_TABLE_NAME);
        try {
            return jdbcTemplate.query(sql, new ExerciseMapper());

        } catch (EmptyResultDataAccessException e) {
            logger.error("No exercises found");
            return null;
        }
    }

    @Override
    public List<Exercise> getExercisesByEquipment(Equipment equipment) {
        String sql = String.format("SELECT * FROM %s WHERE equipment=?", EXERCISE_TABLE_NAME);
        try {
            return jdbcTemplate.query(sql, new ExerciseMapper(), equipment.name());

        } catch (EmptyResultDataAccessException e) {
            logger.error("No exercises with equipment {} found", equipment);
            return null;
        }
    }

    @Override
    public List<Exercise> getExercisesByMuscleGroup(MuscleGroup muscleGroup) {
        String sql = String.format("SELECT * FROM %s WHERE muscle_group=?", EXERCISE_TABLE_NAME);
        try {
            return jdbcTemplate.query(sql, new ExerciseMapper(), muscleGroup.name());

        } catch (EmptyResultDataAccessException e) {
            logger.error("No exercises with muscle group {} found",  muscleGroup);
            return null;
        }
    }

    private Exercise getExerciseFromDB(String name) throws JsonProcessingException {
        String sql = String.format("SELECT * FROM %s WHERE name=?", EXERCISE_TABLE_NAME);
        try {
            Exercise exercise = jdbcTemplate.queryForObject(sql, new ExerciseMapper(), name);
            String exerciseString = objectMapper.writeValueAsString(exercise);
            assert exercise != null;
            cacheRepository.createCacheEntity(exercise.getName(), exerciseString);

            return exercise;

        } catch (EmptyResultDataAccessException e) {
            logger.error("No exercise with name {} found", name);
            return null;
        }
    }
}
