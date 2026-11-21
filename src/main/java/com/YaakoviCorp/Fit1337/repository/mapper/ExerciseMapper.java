package com.YaakoviCorp.Fit1337.repository.mapper;

import com.YaakoviCorp.Fit1337.model.Equipment;
import com.YaakoviCorp.Fit1337.model.Exercise;
import com.YaakoviCorp.Fit1337.model.MuscleGroup;
import jakarta.annotation.Nonnull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExerciseMapper implements RowMapper<Exercise> {
    @Override
    public Exercise mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        return new Exercise(
                rs.getString("name"),
                MuscleGroup.valueOf(rs.getString("muscle_group")),
                Equipment.valueOf(rs.getString("equipment")),
                rs.getFloat("weight"),
                rs.getString("imagePath")
        );
    }
}

