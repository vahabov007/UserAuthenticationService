package com.vahabvahabov.LoginDemo.model;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String mail;

    private String newPassword;
}
