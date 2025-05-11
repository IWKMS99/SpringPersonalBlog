package com.iwkms.personalBlog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @Size(max = 200)
    private String description;
} 