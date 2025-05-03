package com.iwkms.personalBlog.service;

import com.iwkms.personalBlog.dto.UserRegistrationDto;
import com.iwkms.personalBlog.model.entity.User;
import com.iwkms.personalBlog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.iwkms.personalBlog.config.AppConstants.Roles.ROLE_USER;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    private UserRegistrationDto dto;

    @BeforeEach
    void setUp() {
        dto = new UserRegistrationDto("ivan", "password123");
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
}
