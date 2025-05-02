package com.iwkms.personalBlog.service;

import com.iwkms.personalBlog.dto.UserRegistrationDto;
import com.iwkms.personalBlog.model.User;
import com.iwkms.personalBlog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class UserService {

    private static final String DEFAULT_USER_ROLE = "USER";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerNewUser(UserRegistrationDto registrationDto) throws UserAlreadyExistException {
        userRepository.findByUsername(registrationDto.getUsername())
                .ifPresent(existingUser -> {
                    throw new UserAlreadyExistException("User with username '" + registrationDto.getUsername() + "' already exists.");
                });

        User newUser = buildUserFromDto(registrationDto);

        return userRepository.save(newUser);
    }

    /**
     * Builds a new User entity from the registration DTO.
     *
     * @param registrationDto The user registration data.
     * @return A new User entity populated with data from the DTO.
     */
    private User buildUserFromDto(UserRegistrationDto registrationDto) {
        User newUser = new User();
        newUser.setUsername(registrationDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        newUser.setRoles(Collections.singleton("ROLE_" + DEFAULT_USER_ROLE));
        newUser.setEnabled(true);
        return newUser;
    }

    public static class UserAlreadyExistException extends RuntimeException {
        public UserAlreadyExistException(String message) {
            super(message);
        }
    }
}