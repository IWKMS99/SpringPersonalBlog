package com.iwkms.personalBlog.service;

import com.iwkms.personalBlog.dto.UserRegistrationDto;
import com.iwkms.personalBlog.model.entity.User;
import com.iwkms.personalBlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.iwkms.personalBlog.config.AppConstants.Roles.ROLE_ADMIN;
import static com.iwkms.personalBlog.config.AppConstants.Roles.ROLE_USER;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void registerNewUser(UserRegistrationDto registrationDto) throws UserAlreadyExistException {
        userRepository.findByUsername(registrationDto.getUsername())
                .ifPresent(_ -> {
                    throw new UserAlreadyExistException("User with username '" + registrationDto.getUsername() + "' already exists.");
                });

        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new PasswordMismatchException("Пароли не совпадают");
        }

        User newUser = buildUserFromDto(registrationDto);

        userRepository.save(newUser);
    }

    private User buildUserFromDto(UserRegistrationDto registrationDto) {
        User newUser = new User();
        newUser.setUsername(registrationDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        newUser.setRoles(Collections.singleton(ROLE_USER));
        newUser.setEnabled(true);
        return newUser;
    }
    
    @Transactional
    public User addAdminRole(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с именем '" + username + "' не найден"));
        
        Set<String> roles = new HashSet<>(user.getRoles());
        roles.add(ROLE_ADMIN);
        user.setRoles(roles);
        
        return userRepository.save(user);
    }
    
    @Transactional
    public User removeAdminRole(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с именем '" + username + "' не найден"));
        
        Set<String> roles = new HashSet<>(user.getRoles());
        roles.remove(ROLE_ADMIN);
        user.setRoles(roles);
        
        return userRepository.save(user);
    }

    public static class UserAlreadyExistException extends RuntimeException {
        public UserAlreadyExistException(String message) {
            super(message);
        }
    }
    
    public static class PasswordMismatchException extends RuntimeException {
        public PasswordMismatchException(String message) {
            super(message);
        }
    }
    
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}