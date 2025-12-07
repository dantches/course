package com.onlinestore.service;

import com.onlinestore.dto.UserRegistrationDto;
import com.onlinestore.model.Role;
import com.onlinestore.model.User;
import com.onlinestore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(UserRegistrationDto registrationDto) {
        String username = registrationDto.getUsername() != null ? registrationDto.getUsername().trim() : "";
        String email = registrationDto.getEmail() != null ? registrationDto.getEmail().trim() : "";

        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (registrationDto.getPassword() == null || registrationDto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setEmail(email);
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

