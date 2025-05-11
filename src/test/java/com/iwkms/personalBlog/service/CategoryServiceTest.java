package com.iwkms.personalBlog.service;

import com.iwkms.personalBlog.dto.CategoryDto;
import com.iwkms.personalBlog.model.entity.Category;
import com.iwkms.personalBlog.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private CategoryDto categoryDto;
    private Category category;

    @BeforeEach
    void setUp() {
        categoryDto = new CategoryDto();
        categoryDto.setName("Технологии");
        categoryDto.setDescription("Статьи о технологиях");

        category = new Category();
        category.setId(1L);
        category.setName("Технологии");
        category.setDescription("Статьи о технологиях");
    }

    @Test
    void getAllCategories_shouldReturnList() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        
        List<Category> result = categoryService.getAllCategories();
        
        assertThat(result).hasSize(1).containsExactly(category);
        verify(categoryRepository).findAll();
    }

    @Test
    void getCategoryById_existingId_shouldReturnOptional() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        
        Optional<Category> result = categoryService.getCategoryById(1L);
        
        assertThat(result).contains(category);
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryByName_existingName_shouldReturnOptional() {
        when(categoryRepository.findByName("Технологии")).thenReturn(Optional.of(category));
        
        Optional<Category> result = categoryService.getCategoryByName("Технологии");
        
        assertThat(result).contains(category);
        verify(categoryRepository).findByName("Технологии");
    }

    @Test
    void createCategory_uniqueName_shouldSave() {
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        
        Category result = categoryService.createCategory(categoryDto);
        
        assertThat(result).isEqualTo(category);
        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        Category saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Технологии");
        assertThat(saved.getDescription()).isEqualTo("Статьи о технологиях");
    }

    @Test
    void createCategory_duplicateName_shouldThrow() {
        String categoryName = "Технологии";
        String expectedMessage = "Категория с именем '" + categoryName + "' уже существует";
        when(categoryRepository.existsByName(categoryName)).thenReturn(true);
        
        assertThatThrownBy(() -> categoryService.createCategory(categoryDto))
                .isInstanceOf(CategoryService.CategoryAlreadyExistsException.class)
                .hasMessageContaining(expectedMessage);
    }

    @Test
    void updateCategory_existingCategory_shouldUpdate() {
        CategoryDto updateDto = new CategoryDto();
        updateDto.setName("Новые технологии");
        updateDto.setDescription("Обновленное описание");
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Новые технологии")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        
        Optional<Category> result = categoryService.updateCategory(1L, updateDto);
        
        assertThat(result).isPresent();
        verify(categoryRepository).save(category);
        assertThat(category.getName()).isEqualTo("Новые технологии");
        assertThat(category.getDescription()).isEqualTo("Обновленное описание");
    }

    @Test
    void updateCategory_duplicateName_shouldThrow() {
        CategoryDto updateDto = new CategoryDto();
        String categoryName = "Другая категория";
        updateDto.setName(categoryName);
        String expectedMessage = "Категория с именем '" + categoryName + "' уже существует";
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName(categoryName)).thenReturn(true);
        
        assertThatThrownBy(() -> categoryService.updateCategory(1L, updateDto))
                .isInstanceOf(CategoryService.CategoryAlreadyExistsException.class)
                .hasMessageContaining(expectedMessage);
    }

    @Test
    void deleteCategory_shouldDelete() {
        categoryService.deleteCategory(1L);
        
        verify(categoryRepository).deleteById(1L);
    }
} 