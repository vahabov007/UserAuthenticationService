package com.vahabvahabov.LoginDemo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequest {

    @NotEmpty(message = "Username is required.")
    @Size(min = 3, max = 45, message = "Username must be between 3 and 45 length.")
    private String username;

    @NotEmpty(message = "Password is required.")
    @Size(min = 8, max = 66, message = "Username must be between 8 and 66 length.")
    private String password;
}