package com.iwkms.personalBlog.controller;

import com.iwkms.personalBlog.config.AppConstants;
import com.iwkms.personalBlog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    
    @PostMapping("/users/{username}/make-admin")
    public String makeAdmin(@PathVariable String username, RedirectAttributes redirectAttributes) {
        try {
            userService.addAdminRole(username);
            redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_SUCCESS_MESSAGE, 
                    "Пользователю '" + username + "' назначена роль администратора");
        } catch (UserService.UserNotFoundException e) {
            redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_ERROR_MESSAGE, e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_ERROR_MESSAGE, 
                    "Произошла ошибка при назначении роли администратора");
        }
        return "redirect:/users";
    }
    
    @PostMapping("/users/{username}/remove-admin")
    public String removeAdmin(@PathVariable String username, RedirectAttributes redirectAttributes) {
        try {
            userService.removeAdminRole(username);
            redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_SUCCESS_MESSAGE, 
                    "У пользователя '" + username + "' удалена роль администратора");
        } catch (UserService.UserNotFoundException e) {
            redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_ERROR_MESSAGE, e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_ERROR_MESSAGE, 
                    "Произошла ошибка при удалении роли администратора");
        }
        return "redirect:/users";
    }
} 