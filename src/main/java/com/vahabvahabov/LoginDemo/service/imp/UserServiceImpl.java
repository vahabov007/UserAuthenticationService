package com.vahabvahabov.LoginDemo.service.imp;

import com.vahabvahabov.LoginDemo.dto.AuthRequest;
import com.vahabvahabov.LoginDemo.dto.AuthResponse;
import com.vahabvahabov.LoginDemo.dto.RegisterRequest;
import com.vahabvahabov.LoginDemo.model.User;
import com.vahabvahabov.LoginDemo.repository.UserRepository;
import com.vahabvahabov.LoginDemo.security.jwt.JwtUtil;
import com.vahabvahabov.LoginDemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public Optional<User> findUserByMail(String mail) {
        return userRepository.findByMail(mail);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean isUserExists(String mail) {
        return userRepository.findByMail(mail).isPresent();
    }

    @Override
    public void saveNewUser(User user) {
        userRepository.save(user);
    }

    @Override
    public AuthResponse registerUser(RegisterRequest request) throws Exception {
        validateRequestFields(request);
        validateUserExistence(request);
        validatePassword(request.getPassword(), request.getConfirmPassword());
        User newUser = createUserEntity(request);
        try {
            saveNewUser(newUser);

        }catch (DataAccessException e) {
            throw new Exception("Database problem. Please try again later.");
        }
        String jwt = jwtUtil.generateToken(newUser.getUsername());

        return new AuthResponse("Registration has been successful!", jwt, newUser.getUsername());
    }


    private void validateRequestFields(RegisterRequest request) throws Exception {
        if (request == null || request.getMail() == null || request.getUsername() == null ||
                request.getPassword() == null || request.getConfirmPassword() == null || request.getDateOfBirth() == null) {
            throw new Exception("All fields are required.");
        }
    }

    private void validateUserExistence(RegisterRequest request) throws Exception {
        Optional<User> existingMail = findUserByMail(request.getMail());
        if (existingMail.isPresent()) {
            throw new Exception("This email address is already registered!");
        }

        String lowercaseUsername = request.getUsername().toLowerCase();
        Optional<User> existingUsername = findUserByUsername(lowercaseUsername);
        if (existingUsername.isPresent()) {
            throw new Exception("This username is already registered!");
        }
    }

    private void validatePassword(String password, String confirmPassword) throws Exception {
        if (password.length() < 8) {
            throw new Exception("Password must be at least 8 characters long.");
        }

        if (!password.matches(".*\\d.*")) {
            throw new Exception("Password must contain at least one number.");
        }

        if (!password.equals(confirmPassword)) {
            throw new Exception("The passwords do not match!");
        }
    }

    private User createUserEntity(RegisterRequest request) {
        User newUser = new User();
        String lowercaseUsername = request.getUsername().toLowerCase();

        newUser.setUsername(lowercaseUsername);
        newUser.setMail(request.getMail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setDate_of_birth(request.getDateOfBirth());

        newUser.setEmailVerified(true);

        return newUser;
    }
}
