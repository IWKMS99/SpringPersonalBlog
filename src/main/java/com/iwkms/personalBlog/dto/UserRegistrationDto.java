package com.iwkms.personalBlog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]+$";

    @NotBlank
    @Size(min = 3, max = 20)
    @Pattern(regexp = USERNAME_PATTERN)
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
}