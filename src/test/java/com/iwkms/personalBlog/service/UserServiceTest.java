package com.iwkms.personalBlog.service;

import com.iwkms.personalBlog.config.AppConstants;
import com.iwkms.personalBlog.dto.UserRegistrationDto;
import com.iwkms.personalBlog.model.entity.User;
import com.iwkms.personalBlog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.iwkms.personalBlog.config.AppConstants.Roles.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    private UserRegistrationDto dto;

    @BeforeEach
    void setUp() {
        dto = new UserRegistrationDto();
        dto.setUsername("ivan");
        dto.setPassword("password123");
        dto.setConfirmPassword("password123");
    }

    @Test
    void registerNewUser_uniqueUsername_shouldSave() {
        when(userRepository.findByUsername("ivan")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hash");

        userService.registerNewUser(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getUsername()).isEqualTo("ivan");
        assertThat(saved.getPassword()).isEqualTo("hash");
        assertThat(saved.getRoles()).containsExactly(ROLE_USER);
    }

    @Test
    void registerNewUser_existingUsername_shouldThrow() {
        when(userRepository.findByUsername("ivan"))
                .thenReturn(Optional.of(new User()));
        assertThatThrownBy(() -> userService.registerNewUser(dto))
                .isInstanceOf(UserService.UserAlreadyExistException.class);
    }
    
    @Test
    void registerNewUser_passwordMismatch_shouldThrow() {
        dto.setConfirmPassword("different");
        
        assertThatThrownBy(() -> userService.registerNewUser(dto))
                .isInstanceOf(UserService.PasswordMismatchException.class)
                .hasMessage(AppConstants.Messages.PASSWORD_MISMATCH_MESSAGE);
    }
}
