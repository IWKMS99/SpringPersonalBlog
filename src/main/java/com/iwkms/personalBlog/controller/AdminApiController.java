package com.iwkms.personalBlog.controller;

import com.iwkms.personalBlog.model.entity.User;
import com.iwkms.personalBlog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final UserService userService;
    
    @PostMapping("/users/{username}/make-admin")
    public ResponseEntity<?> makeAdmin(@PathVariable String username) {
        try {
            User user = userService.addAdminRole(username);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Пользователю '" + username + "' назначена роль администратора");
            response.put("username", user.getUsername());
            response.put("roles", user.getRoles());
            return ResponseEntity.ok(response);
        } catch (UserService.UserNotFoundException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Произошла ошибка при назначении роли администратора");
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @PostMapping("/users/{username}/remove-admin")
    public ResponseEntity<?> removeAdmin(@PathVariable String username) {
        try {
            User user = userService.removeAdminRole(username);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "У пользователя '" + username + "' удалена роль администратора");
            response.put("username", user.getUsername());
            response.put("roles", user.getRoles());
            return ResponseEntity.ok(response);
        } catch (UserService.UserNotFoundException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Произошла ошибка при удалении роли администратора");
            return ResponseEntity.internalServerError().body(error);
        }
    }
} 