package com.vahabvahabov.LoginDemo.controller.imp;

import com.vahabvahabov.LoginDemo.controller.UserController;
import com.vahabvahabov.LoginDemo.dto.AuthRequest;
import com.vahabvahabov.LoginDemo.dto.AuthResponse;
import com.vahabvahabov.LoginDemo.repository.UserRepository;
import com.vahabvahabov.LoginDemo.security.jwt.JwtUtil;
import com.vahabvahabov.LoginDemo.service.LoginService;
import com.vahabvahabov.LoginDemo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserControllerImpl implements UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());

            }
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            final String jwt = jwtUtil.generateToken(authRequest.getUsername());
            return ResponseEntity.ok(new AuthResponse(jwt, authRequest.getUsername(), "Login Successfully."));

        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, authRequest.getUsername(), "Login Unsuccessfully. Invalid credentials."));
        }
    }


}
