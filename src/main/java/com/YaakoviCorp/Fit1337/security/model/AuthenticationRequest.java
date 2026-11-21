package com.YaakoviCorp.Fit1337.security.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationRequest(
        @NotBlank(message = "username is required")
        @Size(max = 100, message = "username must be at most 100 characters")
        String username,

        @NotBlank(message = "password is required")
        @Size(max = 72, message = "password must be at most 72 characters")
        String password
) {}