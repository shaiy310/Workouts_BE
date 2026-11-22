package com.YaakoviCorp.Fit1337.controller;

import com.YaakoviCorp.Fit1337.model.FitUser;
import com.YaakoviCorp.Fit1337.model.FitUserRequest;
import com.YaakoviCorp.Fit1337.model.UpdateFitUserProfileRequest;
import com.YaakoviCorp.Fit1337.model.UserProfileResponse;
import com.YaakoviCorp.Fit1337.security.model.AuthenticatedUser;
import com.YaakoviCorp.Fit1337.service.FitUserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/public/user")
public class FitUserController {
    private static final Logger log = LoggerFactory.getLogger(FitUserController.class);

    private final FitUserService fitUserService;

    FitUserController(FitUserService fitUserService) {
        this.fitUserService = fitUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody @Valid FitUserRequest fitUserRequest) {
        FitUser user = fitUserService.createUser(fitUserRequest);
        log.info("controller=user_create outcome=success username={}", user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body("user %s created successfully".formatted(user.getUsername()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        fitUserService.deleteUser(currentUser.username());
        log.info("controller=user_delete outcome=success username={}", currentUser.username());
        return ResponseEntity.ok("user deleted successfully");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        UserProfileResponse userProfile = fitUserService.getProfile(currentUser.username());
        log.debug("controller=profile_read outcome=success username={}", currentUser.username());
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateMyProfile(@RequestBody @Valid UpdateFitUserProfileRequest fitUserRequest,
                                             @AuthenticationPrincipal AuthenticatedUser currentUser) {
        UserProfileResponse profile = fitUserService.updateProfile(currentUser.username(), fitUserRequest);
        log.info("controller=profile_update outcome=success username={}", currentUser.username());
        return ResponseEntity.ok(profile);
    }
}

