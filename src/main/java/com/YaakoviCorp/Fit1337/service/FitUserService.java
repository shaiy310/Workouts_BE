package com.YaakoviCorp.Fit1337.service;

import com.YaakoviCorp.Fit1337.model.FitUser;
import com.YaakoviCorp.Fit1337.model.UpdateFitUserProfileRequest;
import com.YaakoviCorp.Fit1337.model.UserProfileResponse;
import com.YaakoviCorp.Fit1337.model.FitUserRequest;
import com.YaakoviCorp.Fit1337.repository.FitUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.acls.model.AlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class FitUserService {
    private static final Logger log = LoggerFactory.getLogger(FitUserService.class);

    private final FitUserRepository repo;
    private final PasswordEncoder passwordEncoder;

    FitUserService(FitUserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public FitUser createUser(FitUserRequest user) {
        FitUser existingFitUser = repo.getUser(user.getUsername());

        if (existingFitUser != null) {
            log.warn("audit=user_create outcome=conflict reason=username_exists username={}", user.getUsername());
            throw new AlreadyExistsException(String.format("A user with name %s already exists.", user.getUsername()));
        }

        FitUser newUser = user.toFitUser();
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        FitUser created = repo.createUser(newUser);

        log.info("audit=user_create outcome=success userId={} username={}", created.getId(), created.getUsername());
        return created;
    }

    @Transactional
    public FitUser updateUser(String username, String password) {
        FitUser fitUser = repo.getUser(username);
        if (fitUser == null) {
            log.warn("audit=password_change outcome=failed reason=user_not_found username={}", username);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("A user with name %s was not found.", username)
            );
        }

        String encodedPassword = passwordEncoder.encode(password);
        FitUser updated = repo.updateUser(username, encodedPassword);

        log.info("audit=password_change outcome=success userId={} username={}", updated.getId(), username);
        return updated;
    }

    @Transactional(readOnly = true)
    public FitUser getUser(String username) {
        return repo.getUser(username);
    }

    @Transactional(readOnly = true)
    public FitUser getUser(UUID id) {
        return repo.getUser(id);
    }

    @Transactional
    public void deleteUser(String username) {
        repo.deleteUser(username);
        log.info("audit=user_delete outcome=success username={}", username);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String username) {
        FitUser fitUser = repo.getUser(username);
        if (fitUser == null) {
            log.warn("audit=profile_read outcome=failed reason=user_not_found username={}", username);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("A user with name %s was not found.", username)
            );
        }
        return fitUser.toUserProfileResponse();
    }

    @Transactional
    public UserProfileResponse updateProfile(String username, UpdateFitUserProfileRequest fitUserRequest) {
        FitUser fitUser = repo.getUser(username);
        if (fitUser == null) {
            log.warn("audit=profile_update outcome=failed reason=user_not_found username={}", username);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("A user with name %s was not found.", username)
            );
        }

        repo.updateProfile(username, fitUserRequest);
        FitUser updatedFitUser = repo.getUser(username);

        log.info("audit=profile_update outcome=success userId={} username={}", updatedFitUser.getId(), username);
        return updatedFitUser.toUserProfileResponse();
    }
}
