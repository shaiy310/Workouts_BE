package com.YaakoviCorp.Fit1337.controller;

import com.YaakoviCorp.Fit1337.model.FitUser;
import com.YaakoviCorp.Fit1337.model.FitUserRequest;
import com.YaakoviCorp.Fit1337.model.UpdateFitUserProfileRequest;
import com.YaakoviCorp.Fit1337.model.UserProfileResponse;
import com.YaakoviCorp.Fit1337.security.model.AuthenticatedUser;
import com.YaakoviCorp.Fit1337.service.FitUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/public/user")
public class FitUserController {
    private final FitUserService fitUserService;

    FitUserController(FitUserService fitUserService) {
        this.fitUserService = fitUserService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody @Valid FitUserRequest fitUserRequest) {
        FitUser user = fitUserService.createUser(fitUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("user %s created successfully".formatted(user.getUsername()));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        fitUserService.deleteUser(currentUser.username());
        return ResponseEntity.ok("user deleted successfully");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        UserProfileResponse userProfile = fitUserService.getProfile(currentUser.username());
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateMyProfile(@RequestBody @Valid UpdateFitUserProfileRequest fitUserRequest,
                                             @AuthenticationPrincipal AuthenticatedUser currentUser) {
        return ResponseEntity.ok(fitUserService.updateProfile(currentUser.username(), fitUserRequest));
    }
}

