package com.vahabvahabov.LoginDemo.controller;

import com.vahabvahabov.LoginDemo.dto.PinVerificationRequest; // DTO paketini istifadə edirik
import com.vahabvahabov.LoginDemo.dto.RegisterRequest; // DTO paketini istifadə edirik
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

public interface VerificationController {

    public ResponseEntity<?> sendPin(@RequestBody Map<String, String> request);

    public ResponseEntity<?> verifyPin(@RequestBody PinVerificationRequest request);

    public ResponseEntity<?> completeRegistration(@RequestBody RegisterRequest request) throws Exception;

}