package com.YaakoviCorp.Fit1337.repository;

import com.YaakoviCorp.Fit1337.model.FitUser;
import com.YaakoviCorp.Fit1337.model.UpdateFitUserProfileRequest;
import com.YaakoviCorp.Fit1337.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class FitUserRepositoryImpl implements FitUserRepository {
    static final String USERS_TABLE_NAME = "users";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public FitUser createUser(FitUser fitUser) {
        String sql = String.format("INSERT INTO %s (username, password, active, roles, permissions) VALUES (?, ?, ?, ?, ?)", USERS_TABLE_NAME);
        jdbcTemplate.update(sql,
                fitUser.getUsername(),
                fitUser.getPassword(),
                fitUser.getActive(),
                fitUser.getRoles(),
                fitUser.getPermissions()
        );

        return getUser(fitUser.getUsername());
    }

    @Override
    public FitUser getUser(String username) {
        String sql = String.format("SELECT * FROM %s WHERE username=?", USERS_TABLE_NAME);
        try {
            return jdbcTemplate.queryForObject(sql,
                    new UserMapper(),
                    username);
        } catch (EmptyResultDataAccessException exc) {
            return null;
        }
    }

    @Override
    public FitUser getUser(UUID id) {
        String sql = String.format("SELECT * FROM %s WHERE id=?", USERS_TABLE_NAME);
        try {
            return jdbcTemplate.queryForObject(sql,
                    new UserMapper(),
                    id);
        } catch (EmptyResultDataAccessException exc) {
            return null;
        }
    }

    @Override
    public FitUser updateUser(String username, String password) {
        String sql = String.format("UPDATE %s SET password=? WHERE username=?", USERS_TABLE_NAME);
        jdbcTemplate.update(sql, password, username);

        return getUser(username);
    }

    @Override
    public void deleteUser(String username) {
        String sql = String.format("DELETE FROM %s WHERE username = ?", USERS_TABLE_NAME);
        jdbcTemplate.update(sql, username);
    }

    @Override
    public void updateProfile(String username, UpdateFitUserProfileRequest fitUserRequest) {
        String sql = String.format("UPDATE %s SET first_name=?, last_name=?, email=? WHERE username=?", USERS_TABLE_NAME);
        jdbcTemplate.update(sql,
                fitUserRequest.getFirstName(),
                fitUserRequest.getLastName(),
                fitUserRequest.getEmail(),
                username
                );
    }
}
