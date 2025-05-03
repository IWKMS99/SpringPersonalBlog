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

import static com.iwkms.personalBlog.config.AppConstants.Attributes.USER_DTO_ATTRIBUTE;
import static com.iwkms.personalBlog.config.AppConstants.Messages.*;
import static com.iwkms.personalBlog.config.AppConstants.Views.*;

@Controller
@RequestMapping("/user")
public class UserController {

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