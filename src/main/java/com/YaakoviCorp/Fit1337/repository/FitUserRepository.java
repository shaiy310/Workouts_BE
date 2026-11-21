package com.YaakoviCorp.Fit1337.repository;

import com.YaakoviCorp.Fit1337.model.FitUser;
import com.YaakoviCorp.Fit1337.model.UpdateFitUserProfileRequest;

import java.util.UUID;

public interface FitUserRepository {
    FitUser createUser(FitUser username);
    FitUser getUser(String username);
    FitUser getUser(UUID id);
    FitUser updateUser(String username, String password);
    void deleteUser(String username);

    void updateProfile(String username, UpdateFitUserProfileRequest fitUserRequest);

}

