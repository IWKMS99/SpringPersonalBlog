package com.iwkms.personalBlog.controller;

import com.iwkms.personalBlog.config.AppConstants;
import com.iwkms.personalBlog.dto.UserRegistrationDto;
import com.iwkms.personalBlog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        Mockito.reset(userService);
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testDisplayLoginPage() throws Exception {
        mockMvc.perform(get("/user/login"))
                .andExpect(status().isOk())
                .andExpect(view().name(AppConstants.Views.LOGIN_VIEW));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testDisplayLogoutConfirmation() throws Exception {
        mockMvc.perform(get("/user/logout"))
                .andExpect(status().isOk())
                .andExpect(view().name(AppConstants.Views.LOGOUT_VIEW));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testDisplayRegistrationForm() throws Exception {
        mockMvc.perform(get("/user/register"))
                .andExpect(status().isOk())
                .andExpect(view().name(AppConstants.Views.REGISTRATION_VIEW))
                .andExpect(model().attributeExists(AppConstants.Attributes.USER_DTO_ATTRIBUTE));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testProcessRegistrationSuccess() throws Exception {
        doNothing().when(userService).registerNewUser(any(UserRegistrationDto.class));

        mockMvc.perform(post("/user/register")
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("confirmPassword", "password123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"))
                .andExpect(flash().attribute(AppConstants.Messages.REGISTRATION_SUCCESS_KEY, AppConstants.Messages.SUCCESS_MESSAGE));

        verify(userService).registerNewUser(any(UserRegistrationDto.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testProcessRegistrationValidationErrors() throws Exception {
        mockMvc.perform(post("/user/register")
                        .param("username", "")
                        .param("password", "short")
                        .param("confirmPassword", "short")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AppConstants.Views.REGISTRATION_VIEW))
                .andExpect(model().attributeHasFieldErrors(AppConstants.Attributes.USER_DTO_ATTRIBUTE, "username", "password"));
    }
    
    @Test
    @WithMockUser(username = "testuser")
    public void testProcessRegistrationPasswordMismatch() throws Exception {
        mockMvc.perform(post("/user/register")
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("confirmPassword", "different")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AppConstants.Views.REGISTRATION_VIEW))
                .andExpect(model().attributeHasFieldErrors(AppConstants.Attributes.USER_DTO_ATTRIBUTE, "confirmPassword"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testProcessRegistrationUserAlreadyExists() throws Exception {
        String username = "existinguser";
        String errorMessage = "User with username '" + username + "' already exists.";
        doThrow(new UserService.UserAlreadyExistException(errorMessage))
                .when(userService).registerNewUser(any(UserRegistrationDto.class));

        mockMvc.perform(post("/user/register")
                        .param("username", username)
                        .param("password", "password123")
                        .param("confirmPassword", "password123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/register?error"))
                .andExpect(flash().attribute(AppConstants.Messages.REGISTRATION_ERROR_KEY, errorMessage));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testProcessRegistrationGenericException() throws Exception {
        doThrow(new RuntimeException("Some error"))
                .when(userService).registerNewUser(any(UserRegistrationDto.class));

        mockMvc.perform(post("/user/register")
                        .param("username", "newuser")
                        .param("password", "password123")
                        .param("confirmPassword", "password123")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/register?error"))
                .andExpect(flash().attribute(AppConstants.Messages.REGISTRATION_ERROR_KEY, AppConstants.Messages.GENERIC_ERROR_MESSAGE));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }
}