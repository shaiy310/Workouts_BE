package com.YaakoviCorp.Fit1337.service;

import com.YaakoviCorp.Fit1337.security.CustomUserDetailsService;
import com.YaakoviCorp.Fit1337.security.model.AuthenticationRequest;
import com.YaakoviCorp.Fit1337.security.model.AuthenticationResponse;
import com.YaakoviCorp.Fit1337.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    AuthenticationService(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }
    
    public AuthenticationResponse createAuthenticationToken(AuthenticationRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.username());
            AuthenticationResponse response = new AuthenticationResponse(jwtUtil.generateToken(userDetails));
            log.info("service=auth_login outcome=success username={}", authRequest.username());
            return response;
        } catch (AuthenticationException ex) {
            log.warn("service=auth_login outcome=failed reason=bad_credentials username={}", authRequest.username());
            throw ex;
        }
    }
}
