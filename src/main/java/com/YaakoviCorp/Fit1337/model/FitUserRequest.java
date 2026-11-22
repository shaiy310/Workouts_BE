package com.YaakoviCorp.Fit1337.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FitUserRequest
{
    @NotBlank(message = "username is required")
    @Size(min = 3, max = 100, message = "username must be between 3 and 100 characters")
    private String username;

    @NotBlank(message = "password is required")
    @Size(min = 4, max = 72, message = "password must be between 4 and 72 characters")
    private String password;

    public FitUserRequest() {
        this.username = "";
        this.password = "";
    }

    public FitUserRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public FitUser toFitUser() {
        return new FitUser(
                null,
                this.username,
                this.password,
                1,
                "USER",
                "",
                "",
                "",
                ""
        );
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
