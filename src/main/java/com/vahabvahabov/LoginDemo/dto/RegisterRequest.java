package com.vahabvahabov.LoginDemo.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class RegisterRequest {

    private String username;

    private String mail;

    private String password;

    private String confirmPassword;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;

}
