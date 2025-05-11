package com.iwkms.personalBlog.controller;

import com.iwkms.personalBlog.dto.CategoryDto;
import com.iwkms.personalBlog.mapper.CategoryMapper;
import com.iwkms.personalBlog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;
    
    @GetMapping
    public String listCategories(Model model) {
        List<CategoryDto> categories = categoryService.getAllCategories().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
        model.addAttribute("categories", categories);
        return "categories";
    }
    
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute("categoryDto", new CategoryDto());
        return "categoryForm";
    }
    
    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createCategory(@Valid @ModelAttribute("categoryDto") CategoryDto categoryDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "categoryForm";
        }
        
        try {
            categoryService.createCategory(categoryDto);
            redirectAttributes.addFlashAttribute("successMessage", "Категория успешно создана");
            return "redirect:/categories";
        } catch (CategoryService.CategoryAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/categories/new?error";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла ошибка при создании категории");
            return "redirect:/categories/new?error";
        }
    }
    
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showUpdateForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return categoryService.getCategoryById(id)
                .map(category -> {
                    model.addAttribute("categoryDto", categoryMapper.toDto(category));
                    return "categoryEditForm";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Категория не найдена");
                    return "redirect:/categories";
                });
    }
    
    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateCategory(@PathVariable Long id,
                               @Valid @ModelAttribute("categoryDto") CategoryDto categoryDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "categoryEditForm";
        }
        
        try {
            return categoryService.updateCategory(id, categoryDto)
                    .map(category -> {
                        redirectAttributes.addFlashAttribute("successMessage", "Категория успешно обновлена");
                        return "redirect:/categories";
                    })
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute("errorMessage", "Категория не найдена");
                        return "redirect:/categories";
                    });
        } catch (CategoryService.CategoryAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/categories/" + id + "/edit?error";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла ошибка при обновлении категории");
            return "redirect:/categories/" + id + "/edit?error";
        }
    }
    
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Категория успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла ошибка при удалении категории");
        }
        return "redirect:/categories";
    }
} 