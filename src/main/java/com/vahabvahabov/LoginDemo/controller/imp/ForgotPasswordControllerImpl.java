package com.vahabvahabov.LoginDemo.controller.imp;

import com.vahabvahabov.LoginDemo.controller.ForgotPasswordController;
import com.vahabvahabov.LoginDemo.model.PinVerificationRequest;
import com.vahabvahabov.LoginDemo.model.ResetPasswordRequest;
import com.vahabvahabov.LoginDemo.model.User;
import com.vahabvahabov.LoginDemo.service.EmailService;
import com.vahabvahabov.LoginDemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/forgot-password") // Düzgün URL annotasiyası
public class ForgotPasswordControllerImpl implements ForgotPasswordController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Map<String, String> pinStorage = new HashMap<>();
    private Map<String, Date> pinExpirationStorage = new HashMap<>();

    @Override
    @PostMapping("/send-pin")
    public ResponseEntity<?> sendPin(@RequestBody Map<String, String> request) {
        String mail = request.get("mail");
        log.info("Received request to send PIN to: {}", mail);

        Optional<User> dbUser = userService.findUserByMail(mail);
        if (dbUser.isEmpty()) {
            log.warn("Attempt to send PIN to unregistered email: {}", mail);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createResponse(false, "This email address is not registered."));
        }

        try {
            String pin = String.format("%06d", new Random().nextInt(999999));
            Date expirationTime = new Date(System.currentTimeMillis() + 10 * 60 * 1000);

            pinStorage.put(mail, pin);
            pinExpirationStorage.put(mail, expirationTime);

            emailService.sendPinToEmail(mail, pin);
            log.info("PIN sent successfully to: {}", mail);
            return ResponseEntity.ok(createResponse(true, "A PIN code has been sent to your email address."));
        } catch (Exception e) {
            log.error("Failed to send PIN to: {}", mail, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createResponse(false, "Failed to send PIN. Please try again."));
        }
    }

    @Override
    @PostMapping("/verify-pin")
    public ResponseEntity<?> verifyPin(@RequestBody PinVerificationRequest request) {
        String mail = request.getMail();
        String pin = request.getPin();

        String storedPin = pinStorage.get(mail);
        Date expirationTime = pinExpirationStorage.get(mail);

        if (storedPin != null && storedPin.equals(pin) && new Date().before(expirationTime)) {
            pinStorage.remove(mail);
            pinExpirationStorage.remove(mail);
            return ResponseEntity.ok(createResponse(true, "Pin code confirmed."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponse(false, "The PIN code you entered is incorrect or has expired."));
    }

    @Override
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        String mail = request.getMail();
        String newPassword = request.getNewPassword();
        log.info("Received password reset request for email: {}", mail);

        if (newPassword == null || newPassword.length() < 8) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponse(false, "Password must be at least 8 characters long."));
        }

        if (!newPassword.matches(".*\\d.*")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponse(false, "Password must contain at least one number."));
        }

        Optional<User> optional = userService.findUserByMail(mail);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createResponse(false, "User not found."));
        }

        User dbUser = optional.get();

        if (passwordEncoder.matches(newPassword, dbUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createResponse(false, "The new password cannot be the same as the old one."));
        }

        dbUser.setPassword(passwordEncoder.encode(newPassword));
        userService.saveNewUser(dbUser);

        log.info("Password successfully updated for user: {}", mail);
        return ResponseEntity.ok(createResponse(true, "Password has been successfully updated."));
    }

    private Map<String, Object> createResponse(boolean success, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        return response;
    }
}