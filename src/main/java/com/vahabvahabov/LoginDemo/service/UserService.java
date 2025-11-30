package com.vahabvahabov.LoginDemo.service;

import com.vahabvahabov.LoginDemo.dto.AuthRequest;
import com.vahabvahabov.LoginDemo.dto.AuthResponse;
import com.vahabvahabov.LoginDemo.dto.RegisterRequest;
import com.vahabvahabov.LoginDemo.model.User;
import java.util.Optional;

public interface UserService {
    Optional<User> findUserByMail(String mail);

    Optional<User> findUserByUsername(String username);

    boolean isUserExists(String mail);

    void saveNewUser(User user);

    AuthResponse registerUser(RegisterRequest request) throws Exception;
}