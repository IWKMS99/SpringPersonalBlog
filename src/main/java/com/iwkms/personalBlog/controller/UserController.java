package com.iwkms.personalBlog.controller;

import com.iwkms.personalBlog.dto.UserRegistrationDto;
import com.iwkms.personalBlog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/logout")
    public String getLogoutPage() {
        return "logout";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("userDto") UserRegistrationDto registrationDto,
                                      RedirectAttributes redirectAttributes) {
        try {
            userService.registerNewUser(registrationDto);
            redirectAttributes.addFlashAttribute("registrationSuccess", "Регистрация прошла успешно! Теперь вы можете войти.");
            return "redirect:/login";
        } catch (UserService.UserAlreadyExistException e) {
            redirectAttributes.addFlashAttribute("registrationError", e.getMessage());
            redirectAttributes.addFlashAttribute("userDto", registrationDto);
            return "redirect:/register?error";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("registrationError", "Произошла ошибка при регистрации.");
            return "redirect:/register?error";
        }
    }
}