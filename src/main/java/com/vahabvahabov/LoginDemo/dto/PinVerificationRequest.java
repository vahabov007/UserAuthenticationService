package com.vahabvahabov.LoginDemo.dto;

import lombok.Data;

@Data
public class PinVerificationRequest {

    private String mail;

    private String pin;
}