package com.iwkms.personalBlog.mapper;

import com.iwkms.personalBlog.dto.PostDto;
import com.iwkms.personalBlog.model.entity.Category;
import com.iwkms.personalBlog.model.entity.Post;
import com.iwkms.personalBlog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostMapper {
    
    private final CategoryRepository categoryRepository;
    
    public PostDto toDto(Post post) {
        PostDto dto = new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toSet()),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getAuthor() != null ? post.getAuthor().getUsername() : null
        );
        return dto;
    }

    public Post toEntity(PostDto dto) {
        Post post = new Post();
        post.setId(dto.getId());
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        
        if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>();
            for (Long categoryId : dto.getCategoryIds()) {
                categoryRepository.findById(categoryId).ifPresent(categories::add);
            }
            post.setCategories(categories);
        }
        
        return post;
    }
}
