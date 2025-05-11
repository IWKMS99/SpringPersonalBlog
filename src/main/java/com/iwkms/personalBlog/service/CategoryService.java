package com.iwkms.personalBlog.service;

import com.iwkms.personalBlog.dto.CategoryDto;
import com.iwkms.personalBlog.model.entity.Category;
import com.iwkms.personalBlog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
    
    @Transactional
    public Category createCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new CategoryAlreadyExistsException("Категория с именем '" + categoryDto.getName() + "' уже существует");
        }
        
        Category category = new Category();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        
        return categoryRepository.save(category);
    }
    
    @Transactional
    public Optional<Category> updateCategory(Long id, CategoryDto categoryDto) {
        return categoryRepository.findById(id)
                .map(existingCategory -> {
                    // Проверяем, не занято ли имя другой категорией
                    if (!existingCategory.getName().equals(categoryDto.getName()) && 
                            categoryRepository.existsByName(categoryDto.getName())) {
                        throw new CategoryAlreadyExistsException("Категория с именем '" + categoryDto.getName() + "' уже существует");
                    }
                    
                    existingCategory.setName(categoryDto.getName());
                    existingCategory.setDescription(categoryDto.getDescription());
                    return categoryRepository.save(existingCategory);
                });
    }
    
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
    
    public static class CategoryAlreadyExistsException extends RuntimeException {
        public CategoryAlreadyExistsException(String message) {
            super(message);
        }
    }
} 