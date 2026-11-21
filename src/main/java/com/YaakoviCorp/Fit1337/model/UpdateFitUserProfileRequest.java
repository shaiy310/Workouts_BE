package com.YaakoviCorp.Fit1337.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateFitUserProfileRequest {
    @Size(max = 50, message = "firstName must be at most 50 characters")
    private String firstName;

    @Size(max = 50, message = "lastName must be at most 50 characters")
    private String lastName;

    @Email(message = "email must be a valid email address")
    @Size(max = 50, message = "email must be at most 50 characters")
    private String email;

    public UpdateFitUserProfileRequest(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
