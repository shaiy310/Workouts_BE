package com.YaakoviCorp.Fit1337.repository;

import com.YaakoviCorp.Fit1337.model.FitUser;
import com.YaakoviCorp.Fit1337.model.UpdateFitUserProfileRequest;
import com.YaakoviCorp.Fit1337.repository.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class FitUserRepositoryImpl implements FitUserRepository {
    private static final Logger log = LoggerFactory.getLogger(FitUserRepositoryImpl.class);
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

        FitUser created = getUser(fitUser.getUsername());
        log.debug("repository=user_create outcome=success username={}", created.getUsername());
        return created;
    }

    @Override
    public FitUser getUser(String username) {
        String sql = String.format("SELECT * FROM %s WHERE username=?", USERS_TABLE_NAME);
        try {
            return jdbcTemplate.queryForObject(sql,
                    new UserMapper(),
                    username);
        } catch (EmptyResultDataAccessException exc) {
            log.debug("repository=user_read outcome=not_found username={}", username);
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
            log.debug("repository=user_read outcome=not_found userId={}", id);
            return null;
        }
    }

    @Override
    public FitUser updateUser(String username, String password) {
        String sql = String.format("UPDATE %s SET password=? WHERE username=?", USERS_TABLE_NAME);
        jdbcTemplate.update(sql, password, username);

        FitUser updated = getUser(username);
        log.debug("repository=user_password_update outcome=success username={}", username);
        return updated;
    }

    @Override
    public void deleteUser(String username) {
        String sql = String.format("DELETE FROM %s WHERE username = ?", USERS_TABLE_NAME);
        jdbcTemplate.update(sql, username);
        log.debug("repository=user_delete outcome=success username={}", username);
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
        log.debug("repository=user_profile_update outcome=success username={}", username);
    }
}
