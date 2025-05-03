package com.iwkms.personalBlog.controller;

import com.iwkms.personalBlog.dto.UserRegistrationDto;
import com.iwkms.personalBlog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {

    // View names as constants
    private static final String LOGIN_VIEW = "login";
    private static final String LOGOUT_VIEW = "logout";
    private static final String REGISTRATION_VIEW = "registration";

    // Message constants
    private static final String REGISTRATION_SUCCESS_KEY = "registrationSuccess";
    private static final String REGISTRATION_ERROR_KEY = "registrationError";
    private static final String SUCCESS_MESSAGE = "Регистрация прошла успешно! Теперь вы можете войти.";
    private static final String GENERIC_ERROR_MESSAGE = "Произошла ошибка при регистрации.";

    // Form attribute name
    private static final String USER_DTO_ATTRIBUTE = "userDto";

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String displayLoginPage() {
        return LOGIN_VIEW;
    }

    @GetMapping("/logout")
    public String displayLogoutConfirmation() {
        return LOGOUT_VIEW;
    }

    @GetMapping("/register")
    public String displayRegistrationForm(Model model) {
        model.addAttribute(USER_DTO_ATTRIBUTE, new UserRegistrationDto());
        return REGISTRATION_VIEW;
    }

    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute(USER_DTO_ATTRIBUTE) UserRegistrationDto registrationDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return REGISTRATION_VIEW;
        }

        try {
            userService.registerNewUser(registrationDto);
            redirectAttributes.addFlashAttribute(REGISTRATION_SUCCESS_KEY, SUCCESS_MESSAGE);
            return "redirect:/user/login";
        } catch (UserService.UserAlreadyExistException e) {
            redirectAttributes.addFlashAttribute(REGISTRATION_ERROR_KEY, e.getMessage());
            return "redirect:/user/register?error";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(REGISTRATION_ERROR_KEY, GENERIC_ERROR_MESSAGE);
            return "redirect:/user/register?error";
        }
    }
}