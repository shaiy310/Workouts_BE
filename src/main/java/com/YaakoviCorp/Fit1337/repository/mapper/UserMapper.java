package com.YaakoviCorp.Fit1337.repository.mapper;

import com.YaakoviCorp.Fit1337.model.FitUser;
import jakarta.annotation.Nonnull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserMapper implements RowMapper<FitUser> {
    @Override
    public FitUser mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        return new FitUser(
                rs.getObject("id", UUID.class),
                rs.getString("username"),
                rs.getString("password"),
                rs.getInt("active"),
                rs.getString("roles"),
                rs.getString("permissions"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("email")
        );
    }
}
