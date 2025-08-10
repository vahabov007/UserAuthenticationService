package com.vahabvahabov.LoginDemo.service.imp;

import com.vahabvahabov.LoginDemo.model.User;
import com.vahabvahabov.LoginDemo.repository.UserRepository;
import com.vahabvahabov.LoginDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;


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
}
