package com.YaakoviCorp.Fit1337.controller;

import com.YaakoviCorp.Fit1337.security.model.AuthenticationRequest;
import com.YaakoviCorp.Fit1337.service.AuthenticationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/public")
public class AuthenticationController {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authService;

    AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody @Valid AuthenticationRequest authRequest) {
        String username = authRequest.username();
        try {
            var response = authService.createAuthenticationToken(authRequest);
            log.info("controller=auth_login outcome=success username={}", username);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException ex) {
            log.warn("controller=auth_login outcome=failed reason=bad_credentials username={}", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect credentials");
        } catch (Exception ex) {
            log.error("controller=auth_login outcome=error username={}", username, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication error");
        }
    }
}


