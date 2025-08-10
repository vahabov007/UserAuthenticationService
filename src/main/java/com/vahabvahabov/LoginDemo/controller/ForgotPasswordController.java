package com.vahabvahabov.LoginDemo.controller;

import com.vahabvahabov.LoginDemo.model.PinVerificationRequest;
import com.vahabvahabov.LoginDemo.model.ResetPasswordRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ForgotPasswordController {

    public ResponseEntity<?> sendPin(Map<String, String> request);

    public ResponseEntity<?> verifyPin(PinVerificationRequest request);

    public ResponseEntity<?> resetPassword(ResetPasswordRequest request);
}
