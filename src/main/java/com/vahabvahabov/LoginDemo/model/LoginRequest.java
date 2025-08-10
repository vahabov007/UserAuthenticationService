package com.vahabvahabov.LoginDemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class LoginRequest {

    private String usernameOrMail;

    private String password;
}
