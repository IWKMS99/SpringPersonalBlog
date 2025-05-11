package com.iwkms.personalBlog.dto;

import com.iwkms.personalBlog.validation.PasswordMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import static com.iwkms.personalBlog.config.AppConstants.Validation.USERNAME_PATTERN;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatch(password = "password", confirmPassword = "confirmPassword", message = "Пароли не совпадают")
public class UserRegistrationDto {

    @NotBlank
    @Size(min = 3, max = 20)
    @Pattern(regexp = USERNAME_PATTERN)
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
    
    @NotBlank
    private String confirmPassword;
}