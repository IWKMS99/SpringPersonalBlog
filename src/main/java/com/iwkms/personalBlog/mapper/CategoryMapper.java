package com.iwkms.personalBlog.mapper;

import com.iwkms.personalBlog.dto.CategoryDto;
import com.iwkms.personalBlog.model.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    public Category toEntity(CategoryDto dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }
} 