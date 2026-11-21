package com.YaakoviCorp.Fit1337.service;

import com.YaakoviCorp.Fit1337.security.CustomUserDetailsService;
import com.YaakoviCorp.Fit1337.security.model.AuthenticationRequest;
import com.YaakoviCorp.Fit1337.security.model.AuthenticationResponse;
import com.YaakoviCorp.Fit1337.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    public AuthenticationResponse createAuthenticationToken(AuthenticationRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
        } catch (Exception exc) {
            throw new Exception("Invalid username and/or password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.username());
        return new AuthenticationResponse(jwtUtil.generateToken(userDetails));
    }
}
