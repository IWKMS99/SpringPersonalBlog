package com.iwkms.personalBlog.service;

import com.iwkms.personalBlog.dto.UserRegistrationDto;
import com.iwkms.personalBlog.model.User;
import com.iwkms.personalBlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final String DEFAULT_USER_ROLE = "USER";
    private static final String ROLE_PREFIX = "ROLE_";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerNewUser(UserRegistrationDto registrationDto) throws UserAlreadyExistException {
        userRepository.findByUsername(registrationDto.getUsername())
                .ifPresent(existingUser -> {
                    throw new UserAlreadyExistException("User with username '" + registrationDto.getUsername() + "' already exists.");
                });

        User newUser = buildUserFromDto(registrationDto);

        userRepository.save(newUser);
    }

    private User buildUserFromDto(UserRegistrationDto registrationDto) {
        User newUser = new User();
        newUser.setUsername(registrationDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        newUser.setRoles(Collections.singleton(ROLE_PREFIX + DEFAULT_USER_ROLE));
        newUser.setEnabled(true);
        return newUser;
    }

    public static class UserAlreadyExistException extends RuntimeException {
        public UserAlreadyExistException(String message) {
            super(message);
        }
    }
}