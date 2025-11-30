package com.vahabvahabov.LoginDemo.controller;

import com.vahabvahabov.LoginDemo.dto.AuthRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public interface UserController {

    public ResponseEntity<?> login(AuthRequest authRequest, BindingResult bindingResult);
}
