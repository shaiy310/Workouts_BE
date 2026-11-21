package com.YaakoviCorp.Fit1337.security;

import com.YaakoviCorp.Fit1337.model.FitUser;
import com.YaakoviCorp.Fit1337.security.model.AuthenticatedUser;
import com.YaakoviCorp.Fit1337.service.FitUserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final FitUserService fitUserService;

    public CustomUserDetailsService(FitUserService fitUserService) {
        this.fitUserService = fitUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        FitUser fitUser = fitUserService.getUser(username);

        if (fitUser == null) {
            throw new UsernameNotFoundException("User %s cannot be found.".formatted(username));
        }

        // Map roles from fitUser if you store them, or default to ROLE_USER
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new AuthenticatedUser(
                fitUser.getId(),
                fitUser.getUsername(),
                fitUser.getPassword(),
                authorities
        );
    }
}
