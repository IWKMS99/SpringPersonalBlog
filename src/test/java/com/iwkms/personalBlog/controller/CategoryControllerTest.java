package com.iwkms.personalBlog.controller;

import com.iwkms.personalBlog.config.SecurityConfig;
import com.iwkms.personalBlog.dto.CategoryDto;
import com.iwkms.personalBlog.mapper.CategoryMapper;
import com.iwkms.personalBlog.model.entity.Category;
import com.iwkms.personalBlog.service.CategoryService;
import com.iwkms.personalBlog.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import(SecurityConfig.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(categoryService, categoryMapper, customUserDetailsService);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testListCategories() throws Exception {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Технологии");
        
        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Наука");
        
        List<Category> categories = Arrays.asList(category1, category2);
        
        CategoryDto dto1 = new CategoryDto();
        dto1.setId(1L);
        dto1.setName("Технологии");
        
        CategoryDto dto2 = new CategoryDto();
        dto2.setId(2L);
        dto2.setName("Наука");
        
        when(categoryService.getAllCategories()).thenReturn(categories);
        when(categoryMapper.toDto(category1)).thenReturn(dto1);
        when(categoryMapper.toDto(category2)).thenReturn(dto2);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/categories/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("categoryForm"))
                .andExpect(model().attributeExists("categoryDto"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateCategorySuccess() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Новая категория");
        categoryDto.setDescription("Описание");
        
        Category category = new Category();
        category.setId(1L);
        category.setName("Новая категория");
        category.setDescription("Описание");
        
        when(categoryService.createCategory(any(CategoryDto.class))).thenReturn(category);

        mockMvc.perform(post("/categories/new")
                        .param("name", "Новая категория")
                        .param("description", "Описание")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"))
                .andExpect(flash().attribute("successMessage", "Категория успешно создана"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateCategoryDuplicate() throws Exception {
        when(categoryService.createCategory(any(CategoryDto.class)))
                .thenThrow(new CategoryService.CategoryAlreadyExistsException("Категория с именем 'Существующая' уже существует"));

        mockMvc.perform(post("/categories/new")
                        .param("name", "Существующая")
                        .param("description", "Описание")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories/new?error"))
                .andExpect(flash().attribute("errorMessage", "Категория с именем 'Существующая' уже существует"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testShowUpdateForm() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Категория");
        category.setDescription("Описание");
        
        CategoryDto dto = new CategoryDto();
        dto.setId(1L);
        dto.setName("Категория");
        dto.setDescription("Описание");
        
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(dto);

        mockMvc.perform(get("/categories/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("categoryEditForm"))
                .andExpect(model().attributeExists("categoryDto"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testShowUpdateFormNotFound() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/categories/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"))
                .andExpect(flash().attribute("errorMessage", "Категория не найдена"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateCategorySuccess() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Обновленная категория");
        category.setDescription("Новое описание");
        
        when(categoryService.updateCategory(eq(1L), any(CategoryDto.class)))
                .thenReturn(Optional.of(category));

        mockMvc.perform(post("/categories/1/edit")
                        .param("id", "1")
                        .param("name", "Обновленная категория")
                        .param("description", "Новое описание")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"))
                .andExpect(flash().attribute("successMessage", "Категория успешно обновлена"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateCategoryDuplicate() throws Exception {
        when(categoryService.updateCategory(eq(1L), any(CategoryDto.class)))
                .thenThrow(new CategoryService.CategoryAlreadyExistsException("Категория с именем 'Существующая' уже существует"));

        mockMvc.perform(post("/categories/1/edit")
                        .param("id", "1")
                        .param("name", "Существующая")
                        .param("description", "Описание")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories/1/edit?error"))
                .andExpect(flash().attribute("errorMessage", "Категория с именем 'Существующая' уже существует"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteCategorySuccess() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(post("/categories/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/categories"))
                .andExpect(flash().attribute("successMessage", "Категория успешно удалена"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testAccessDeniedForNonAdmin() throws Exception {
        mockMvc.perform(get("/categories/new"))
                .andExpect(status().isForbidden());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CategoryService categoryService() {
            return Mockito.mock(CategoryService.class);
        }

        @Bean
        public CategoryMapper categoryMapper() {
            return Mockito.mock(CategoryMapper.class);
        }

        @Bean
        public CustomUserDetailsService customUserDetailsService() {
            return Mockito.mock(CustomUserDetailsService.class);
        }
    }
} 