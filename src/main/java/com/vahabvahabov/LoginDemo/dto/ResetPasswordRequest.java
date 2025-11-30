package com.vahabvahabov.LoginDemo.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {

    private String mail;

    private String newPassword;
}
