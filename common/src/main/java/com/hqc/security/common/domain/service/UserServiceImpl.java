package com.hqc.security.common.domain.service;

import com.hqc.security.common.domain.model.Role;
import com.hqc.security.common.domain.model.User;
import com.hqc.security.common.domain.port.in.UserService;
import com.hqc.security.common.domain.port.out.UserRepository;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.time.LocalDateTime;
import java.util.UUID;

@Named
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Inject
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String username, String email, String passwordHash, Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User(null, username, email, passwordHash, role, LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}
