package com.iwkms.personalBlog.mapper;

import com.iwkms.personalBlog.dto.CategoryDto;
import com.iwkms.personalBlog.model.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryMapperTest {

    private CategoryMapper categoryMapper;
    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        categoryMapper = new CategoryMapper();
        
        category = new Category();
        category.setId(1L);
        category.setName("Технологии");
        category.setDescription("Статьи о технологиях");
        
        categoryDto = new CategoryDto();
        categoryDto.setId(1L);
        categoryDto.setName("Технологии");
        categoryDto.setDescription("Статьи о технологиях");
    }

    @Test
    void toDto_shouldMapAllFields() {
        CategoryDto result = categoryMapper.toDto(category);
        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Технологии");
        assertThat(result.getDescription()).isEqualTo("Статьи о технологиях");
    }

    @Test
    void toEntity_shouldMapAllFields() {
        Category result = categoryMapper.toEntity(categoryDto);
        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Технологии");
        assertThat(result.getDescription()).isEqualTo("Статьи о технологиях");
    }
} 