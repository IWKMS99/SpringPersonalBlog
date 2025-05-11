package com.iwkms.personalBlog.controller;

import com.iwkms.personalBlog.dto.PostDto;
import com.iwkms.personalBlog.mapper.CategoryMapper;
import com.iwkms.personalBlog.mapper.PostMapper;
import com.iwkms.personalBlog.model.entity.Post;
import com.iwkms.personalBlog.model.entity.User;
import com.iwkms.personalBlog.repository.UserRepository;
import com.iwkms.personalBlog.service.CategoryService;
import com.iwkms.personalBlog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.iwkms.personalBlog.config.AppConstants.Attributes.*;
import static com.iwkms.personalBlog.config.AppConstants.Messages.*;
import static com.iwkms.personalBlog.config.AppConstants.Roles.ROLE_ADMIN;
import static com.iwkms.personalBlog.config.AppConstants.Views.*;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;
    private final PostMapper postMapper;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping("/")
    public String listPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage;
        
        if (categoryId != null) {
            postPage = postService.getPostsByCategory(categoryId, pageable);
            model.addAttribute("selectedCategoryId", categoryId);
        } else if (search != null && !search.isEmpty()) {
            postPage = postService.searchPosts(search, pageable);
            model.addAttribute("search", search);
        } else {
            postPage = postService.getAllPosts(pageable);
        }
        
        model.addAttribute(ATTR_POSTS, postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("totalItems", postPage.getTotalElements());
        
        model.addAttribute("categories", categoryService.getAllCategories());
        
        return VIEW_POSTS;
    }

    @GetMapping("/post/{id}")
    public String showPost(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return postService.getPostById(id)
                .map(post -> {
                    model.addAttribute(ATTR_POST, post);
                    return VIEW_POST_DETAILS;
                })
                .orElseGet(() -> handlePostNotFound(redirectAttributes));
    }

    @GetMapping("/post/new")
    public String showCreateForm(Model model) {
        model.addAttribute("postDto", new PostDto());
        model.addAttribute("categories", categoryService.getAllCategories().stream()
                .map(categoryMapper::toDto)
                .toList());
        return VIEW_POST_FORM;
    }

    @PostMapping("/post/new")
    public String createPost(@Valid @ModelAttribute("postDto") PostDto postDto,
                             BindingResult bindingResult,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories().stream()
                    .map(categoryMapper::toDto)
                    .toList());
            return VIEW_POST_FORM;
        }

        try {
            User author = getCurrentUser(authentication);
            Post post = postMapper.toEntity(postDto);
            post.setAuthor(author);
            Post savedPost = postService.createPost(post);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, MSG_POST_CREATED);
            return REDIRECT_POST_DETAIL + savedPost.getId();
        } catch (RuntimeException e) {
            return handleError(redirectAttributes, e.getMessage());
        }
    }

    @GetMapping("/post/{id}/edit")
    public String showUpdateForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return postService.getPostById(id)
                .map(post -> {
                    model.addAttribute("postDto", postMapper.toDto(post));
                    model.addAttribute("categories", categoryService.getAllCategories().stream()
                            .map(categoryMapper::toDto)
                            .toList());
                    return VIEW_POST_EDIT_FORM;
                })
                .orElseGet(() -> handlePostNotFound(redirectAttributes));
    }

    @PostMapping("/post/{id}/edit")
    public String updatePost(@PathVariable Long id,
                         @Valid @ModelAttribute("postDto") PostDto postDto,
                         BindingResult bindingResult,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories().stream()
                    .map(categoryMapper::toDto)
                    .toList());
            return VIEW_POST_EDIT_FORM;
        }

        try {
            User currentUser = getCurrentUser(authentication);
            Post updated = postMapper.toEntity(postDto);
            return postService.updatePost(id, updated, currentUser)
                    .map(_ -> {
                        redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, MSG_POST_UPDATED);
                        return REDIRECT_POST_DETAIL + id;
                    })
                    .orElseGet(() -> handlePostNotFound(redirectAttributes));
        } catch (PostService.UnauthorizedAccessException e) {
            return handleError(redirectAttributes, MSG_UNAUTHORIZED);
        } catch (RuntimeException e) {
            return handleError(redirectAttributes, e.getMessage());
        }
    }

    @PostMapping("/post/{id}/delete")
    public String deletePost(@PathVariable Long id, Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            User currentUser = getCurrentUser(authentication);
            postService.deletePost(id, currentUser);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, MSG_POST_DELETED);
            return REDIRECT_HOME;
        } catch (PostService.UnauthorizedAccessException e) {
            return handleError(redirectAttributes, MSG_UNAUTHORIZED);
        } catch (RuntimeException e) {
            return handleError(redirectAttributes, e.getMessage());
        }
    }

    @GetMapping("/user/posts")
    public String getUserPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication,
            Model model) {
        
        User currentUser = getCurrentUser(authentication);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage = postService.getPostsByAuthor(currentUser, pageable);
        
        model.addAttribute(ATTR_POSTS, postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("totalItems", postPage.getTotalElements());
        
        return "userPosts";
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(ROLE_ADMIN));
    }

    private String handlePostNotFound(RedirectAttributes redirectAttributes) {
        return handleError(redirectAttributes, MSG_POST_NOT_FOUND);
    }

    private String handleError(RedirectAttributes redirectAttributes, String errorMessage) {
        redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, errorMessage);
        return REDIRECT_HOME;
    }

    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(MSG_USER_NOT_FOUND));
    }
}