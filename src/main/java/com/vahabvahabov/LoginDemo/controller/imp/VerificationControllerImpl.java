package com.vahabvahabov.LoginDemo.controller.imp;

import com.vahabvahabov.LoginDemo.controller.VerificationController;
import com.vahabvahabov.LoginDemo.model.PinVerificationRequest;
import com.vahabvahabov.LoginDemo.model.RegisterRequest;
import com.vahabvahabov.LoginDemo.model.User;
import com.vahabvahabov.LoginDemo.service.EmailService;
import com.vahabvahabov.LoginDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.dao.DataAccessException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/register")
public class VerificationControllerImpl implements VerificationController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Map<String, String> pinStorage = new HashMap<>();
    private Map<String, Date> pinExpirationStorage = new HashMap<>();

    @Override
    @PostMapping("/send-pin")
    public ResponseEntity<?> sendPin(@RequestBody Map<String, String> request) {
        String mail = request.get("mail");

        Optional<User> existingUser = userService.findUserByMail(mail);
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body(createResponse(false, "This email address is already registered!"));
        }

        String pin = String.format("%06d", new Random().nextInt(999999));
        Date expirationTime = new Date(System.currentTimeMillis() + 10 * 60 * 1000);

        pinStorage.put(mail, pin);
        pinExpirationStorage.put(mail, expirationTime);

        emailService.sendPinToEmail(mail, pin);

        return ResponseEntity.ok(createResponse(true, "Your code has been sent to your email address."));
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
            return ResponseEntity.ok(createResponse(true, "Email confirmed!"));
        }

        return ResponseEntity.badRequest().body(createResponse(false, "The entered PIN code is incorrect or has expired!"));
    }

    @Override
    @PostMapping("/complete-registration")
    public ResponseEntity<?> completeRegistration(@RequestBody RegisterRequest request) {
        if (request == null || request.getMail() == null || request.getUsername() == null || request.getPassword() == null || request.getConfirmPassword() == null || request.getDateOfBirth() == null) {
            return ResponseEntity.badRequest().body(createResponse(false, "All fields are required."));
        }

        if (userService.findUserByMail(request.getMail()).isPresent()) {
            return ResponseEntity.badRequest().body(createResponse(false, "This email address is already registered!"));
        }

        String lowercaseUsername = request.getUsername().toLowerCase();
        if (userService.findUserByUsername(lowercaseUsername).isPresent()) {
            return ResponseEntity.badRequest().body(createResponse(false, "This username is already registered!"));
        }

        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        if (password.length() < 8) {
            return ResponseEntity.badRequest().body(createResponse(false, "Password must be at least 8 characters long."));
        }

        if (!password.matches(".*\\d.*")) {
            return ResponseEntity.badRequest().body(createResponse(false, "Password must contain at least one number."));
        }

        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(createResponse(false, "The passwords do not match!"));
        }

        User newUser = new User();
        newUser.setUsername(lowercaseUsername);
        newUser.setMail(request.getMail());
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setDate_of_birth(request.getDateOfBirth());
        newUser.setEmailVerified(true);

        try {
            userService.saveNewUser(newUser);
            return ResponseEntity.ok(createResponse(true, "Registration has been successfully!"));
        } catch (DataAccessException e) {
            System.err.println("Database access error during registration: " + e.getMessage());
            return ResponseEntity.badRequest().body(createResponse(false, "There was a problem related to Database. Please try again."));
        } catch (Exception e) {
            System.err.println("Unexpected error during registration: " + e.getMessage());
            return ResponseEntity.badRequest().body(createResponse(false, "Something went wrong."));
        }
    }

    @PostMapping("/resend-pin")
    public ResponseEntity<?> resendPin(@RequestBody Map<String, String> request) {
        String mail = request.get("mail");
        if (mail == null || mail.isEmpty()) {
            return ResponseEntity.badRequest().body(createResponse(false, "Email address not found!"));
        }

        Optional<User> existingUser = userService.findUserByMail(mail);
        if (existingUser.isPresent()) {
        }

        String pin = String.format("%06d", new Random().nextInt(999999));

        Date expirationTime = new Date(System.currentTimeMillis() + 2 * 60 * 1000);

        pinStorage.put(mail, pin);
        pinExpirationStorage.put(mail, expirationTime);

        emailService.sendPinToEmail(mail, pin);

        return ResponseEntity.ok(createResponse(true, "A new PIN has been sent to your email address."));
    }

    private Map<String, Object> createResponse(boolean success, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        return response;
    }
}