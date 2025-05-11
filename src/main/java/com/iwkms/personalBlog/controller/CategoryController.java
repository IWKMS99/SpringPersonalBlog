package com.iwkms.personalBlog.controller;

import com.iwkms.personalBlog.config.AppConstants;
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
        model.addAttribute(AppConstants.Attributes.ATTR_CATEGORIES, categories);
        return AppConstants.Views.VIEW_CATEGORIES;
    }
    
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        model.addAttribute(AppConstants.Attributes.ATTR_CATEGORY_DTO, new CategoryDto());
        return AppConstants.Views.VIEW_CATEGORY_FORM;
    }
    
    @PostMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createCategory(@Valid @ModelAttribute(AppConstants.Attributes.ATTR_CATEGORY_DTO) CategoryDto categoryDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return AppConstants.Views.VIEW_CATEGORY_FORM;
        }
        
        try {
            categoryService.createCategory(categoryDto);
            redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_SUCCESS_MESSAGE, AppConstants.Messages.MSG_CATEGORY_CREATED);
            return AppConstants.Views.REDIRECT_CATEGORIES;
        } catch (CategoryService.CategoryAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_ERROR_MESSAGE, e.getMessage());
            return "redirect:/categories/new?error";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_ERROR_MESSAGE, AppConstants.Messages.MSG_CATEGORY_CREATE_ERROR);
            return "redirect:/categories/new?error";
        }
    }
    
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showUpdateForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return categoryService.getCategoryById(id)
                .map(category -> {
                    model.addAttribute(AppConstants.Attributes.ATTR_CATEGORY_DTO, categoryMapper.toDto(category));
                    return AppConstants.Views.VIEW_CATEGORY_EDIT_FORM;
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_ERROR_MESSAGE, AppConstants.Messages.MSG_CATEGORY_NOT_FOUND);
                    return AppConstants.Views.REDIRECT_CATEGORIES;
                });
    }
    
    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateCategory(@PathVariable Long id,
                               @Valid @ModelAttribute(AppConstants.Attributes.ATTR_CATEGORY_DTO) CategoryDto categoryDto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return AppConstants.Views.VIEW_CATEGORY_EDIT_FORM;
        }
        
        try {
            return categoryService.updateCategory(id, categoryDto)
                    .map(category -> {
                        redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_SUCCESS_MESSAGE, AppConstants.Messages.MSG_CATEGORY_UPDATED);
                        return AppConstants.Views.REDIRECT_CATEGORIES;
                    })
                    .orElseGet(() -> {
                        redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_ERROR_MESSAGE, AppConstants.Messages.MSG_CATEGORY_NOT_FOUND);
                        return AppConstants.Views.REDIRECT_CATEGORIES;
                    });
        } catch (CategoryService.CategoryAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_ERROR_MESSAGE, e.getMessage());
            return "redirect:/categories/" + id + "/edit?error";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_ERROR_MESSAGE, AppConstants.Messages.MSG_CATEGORY_UPDATE_ERROR);
            return "redirect:/categories/" + id + "/edit?error";
        }
    }
    
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_SUCCESS_MESSAGE, AppConstants.Messages.MSG_CATEGORY_DELETED);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(AppConstants.Attributes.ATTR_ERROR_MESSAGE, AppConstants.Messages.MSG_CATEGORY_DELETE_ERROR);
        }
        return AppConstants.Views.REDIRECT_CATEGORIES;
    }
} 