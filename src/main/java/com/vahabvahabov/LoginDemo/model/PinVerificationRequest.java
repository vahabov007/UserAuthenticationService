package com.vahabvahabov.LoginDemo.model;

import lombok.Data;

@Data
public class PinVerificationRequest {

    private String mail;

    private String pin;
}