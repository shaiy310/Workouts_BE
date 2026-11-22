package com.YaakoviCorp.Fit1337.service;

import com.YaakoviCorp.Fit1337.security.CustomUserDetailsService;
import com.YaakoviCorp.Fit1337.security.model.AuthenticationRequest;
import com.YaakoviCorp.Fit1337.security.model.AuthenticationResponse;
import com.YaakoviCorp.Fit1337.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    AuthenticationService(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }
    
    public AuthenticationResponse createAuthenticationToken(AuthenticationRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.username());
        return new AuthenticationResponse(jwtUtil.generateToken(userDetails));
    }
}
