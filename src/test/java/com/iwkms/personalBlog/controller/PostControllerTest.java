package com.iwkms.personalBlog.controller;

import com.iwkms.personalBlog.config.AppConstants;
import com.iwkms.personalBlog.dto.PostDto;
import com.iwkms.personalBlog.mapper.CategoryMapper;
import com.iwkms.personalBlog.mapper.PostMapper;
import com.iwkms.personalBlog.model.entity.Post;
import com.iwkms.personalBlog.model.entity.User;
import com.iwkms.personalBlog.repository.UserRepository;
import com.iwkms.personalBlog.service.CategoryService;
import com.iwkms.personalBlog.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private CategoryMapper categoryMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(postService, userRepository, postMapper, categoryService, categoryMapper);
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testListPosts() throws Exception {
        List<Post> posts = Arrays.asList(new Post(), new Post());
        Page<Post> postPage = new PageImpl<>(posts);
        when(postService.getAllPosts(any(Pageable.class))).thenReturn(postPage);
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name(AppConstants.Views.VIEW_POSTS))
                .andExpect(model().attribute(AppConstants.Attributes.ATTR_POSTS, posts));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testShowPostFound() throws Exception {
        Post post = new Post();
        post.setId(1L);
        when(postService.getPostById(1L)).thenReturn(Optional.of(post));

        mockMvc.perform(get("/post/1"))
                .andExpect(status().isOk())
                .andExpect(view().name(AppConstants.Views.VIEW_POST_DETAILS))
                .andExpect(model().attribute(AppConstants.Attributes.ATTR_POST, post));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testShowPostNotFound() throws Exception {
        when(postService.getPostById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/post/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute(AppConstants.Attributes.ATTR_ERROR_MESSAGE, AppConstants.Messages.MSG_POST_NOT_FOUND));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testShowCreateForm() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());
        
        mockMvc.perform(get("/post/new"))
                .andExpect(status().isOk())
                .andExpect(view().name(AppConstants.Views.VIEW_POST_FORM))
                .andExpect(model().attributeExists("postDto"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testCreatePostSuccess() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setAuthor(user);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(postMapper.toEntity(any(PostDto.class))).thenReturn(post);
        when(postService.createPost(any(Post.class))).thenReturn(post);
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/post/new")
                        .param("title", "Test Title")
                        .param("content", "Test Content")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/1"))
                .andExpect(flash().attribute(AppConstants.Attributes.ATTR_SUCCESS_MESSAGE, AppConstants.Messages.MSG_POST_CREATED));

        verify(postService).createPost(any(Post.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testCreatePostValidationErrors() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());
        
        mockMvc.perform(post("/post/new")
                        .param("title", "")
                        .param("content", "Short")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AppConstants.Views.VIEW_POST_FORM))
                .andExpect(model().attributeHasFieldErrors("postDto", "title", "content"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testShowUpdateFormPostFound() throws Exception {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setCreatedAt(LocalDateTime.now());
        post.setCategories(new HashSet<>());

        PostDto postDto = new PostDto();
        postDto.setId(1L);
        postDto.setTitle("Test Title");
        postDto.setContent("Test Content");
        postDto.setCategoryIds(new HashSet<>());

        when(postService.getPostById(1L)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postDto);
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/post/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name(AppConstants.Views.VIEW_POST_EDIT_FORM))
                .andExpect(model().attributeExists("postDto"))
                .andExpect(model().attribute("postDto", hasProperty("title", is("Test Title"))))
                .andExpect(model().attribute("postDto", hasProperty("content", is("Test Content"))));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testShowUpdateFormPostNotFound() throws Exception {
        when(postService.getPostById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/post/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute(AppConstants.Attributes.ATTR_ERROR_MESSAGE, AppConstants.Messages.MSG_POST_NOT_FOUND));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testUpdatePostSuccess() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        Post existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setAuthor(user);

        Post updatedPost = new Post();
        updatedPost.setTitle("Updated Title");
        updatedPost.setContent("Updated Content");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(postMapper.toEntity(any(PostDto.class))).thenReturn(updatedPost);
        when(postService.updatePost(eq(1L), any(Post.class), eq(user))).thenReturn(Optional.of(updatedPost));
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/post/1/edit")
                        .param("title", "Updated Title")
                        .param("content", "Updated Content")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/1"))
                .andExpect(flash().attribute(AppConstants.Attributes.ATTR_SUCCESS_MESSAGE, AppConstants.Messages.MSG_POST_UPDATED));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testUpdatePostUnauthorized() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        Post post = new Post();
        post.setId(1L);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(postService.getPostById(1L)).thenReturn(Optional.of(post));
        when(postMapper.toEntity(any(PostDto.class))).thenReturn(post);
        when(postService.updatePost(eq(1L), any(Post.class), eq(user)))
                .thenThrow(new PostService.UnauthorizedAccessException("Unauthorized"));
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/post/1/edit")
                        .param("title", "Updated Title")
                        .param("content", "Updated Content")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute(AppConstants.Attributes.ATTR_ERROR_MESSAGE, AppConstants.Messages.MSG_UNAUTHORIZED));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testUpdatePostValidationErrors() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());
        
        mockMvc.perform(post("/post/1/edit")
                        .param("title", "")
                        .param("content", "Short")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(AppConstants.Views.VIEW_POST_EDIT_FORM))
                .andExpect(model().attributeHasFieldErrors("postDto", "title", "content"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testDeletePostSuccess() throws Exception {
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doNothing().when(postService).deletePost(1L, user);

        mockMvc.perform(post("/post/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute(AppConstants.Attributes.ATTR_SUCCESS_MESSAGE, AppConstants.Messages.MSG_POST_DELETED));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testDeletePostUnauthorized() throws Exception {
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doThrow(new PostService.UnauthorizedAccessException("Unauthorized")).when(postService).deletePost(1L, user);

        mockMvc.perform(post("/post/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute(AppConstants.Attributes.ATTR_ERROR_MESSAGE, AppConstants.Messages.MSG_UNAUTHORIZED));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PostService postService() {
            return Mockito.mock(PostService.class);
        }

        @Bean
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }

        @Bean
        public PostMapper postMapper() {
            return Mockito.mock(PostMapper.class);
        }
        
        @Bean
        public CategoryService categoryService() {
            return Mockito.mock(CategoryService.class);
        }
        
        @Bean
        public CategoryMapper categoryMapper() {
            return Mockito.mock(CategoryMapper.class);
        }
    }
}