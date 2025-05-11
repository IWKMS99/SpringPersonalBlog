package com.iwkms.personalBlog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.iwkms.personalBlog.config.AppConstants.Content.POST_TITLE_MAX_LENGTH;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;

    @NotBlank
    @Size(min = 3, max = POST_TITLE_MAX_LENGTH)
    private String title;

    @NotBlank
    @Size(min = 10)
    private String content;
    
    private Set<Long> categoryIds = new HashSet<>();
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String authorUsername;
}
