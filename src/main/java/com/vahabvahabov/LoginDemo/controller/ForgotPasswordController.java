package com.vahabvahabov.LoginDemo.controller;

import com.vahabvahabov.LoginDemo.dto.PinVerificationRequest;
import com.vahabvahabov.LoginDemo.dto.ResetPasswordRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ForgotPasswordController {

    public ResponseEntity<?> sendPin(Map<String, String> request);

    public ResponseEntity<?> verifyPin(PinVerificationRequest request);

    public ResponseEntity<?> resetPassword(ResetPasswordRequest request);
}
