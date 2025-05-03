package com.iwkms.personalBlog.controller;

import com.iwkms.personalBlog.dto.PostDto;
import com.iwkms.personalBlog.mapper.PostMapper;
import com.iwkms.personalBlog.model.entity.Post;
import com.iwkms.personalBlog.model.entity.User;
import com.iwkms.personalBlog.repository.UserRepository;
import com.iwkms.personalBlog.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
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

    @BeforeEach
    void setUp() {
        Mockito.reset(postService, userRepository, postMapper);
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testListPosts() throws Exception {
        List<Post> posts = Arrays.asList(new Post(), new Post());
        when(postService.getAllPosts()).thenReturn(posts);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts"))
                .andExpect(model().attribute("posts", posts));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testShowPostFound() throws Exception {
        Post post = new Post();
        post.setId(1L);
        when(postService.getPostById(1L)).thenReturn(Optional.of(post));

        mockMvc.perform(get("/post/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("postDetails"))
                .andExpect(model().attribute("post", post));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testShowPostNotFound() throws Exception {
        when(postService.getPostById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/post/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("errorMessage", "Post not found"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/post/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("postForm"))
                .andExpect(model().attributeExists("postDto"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testCreatePostSuccess() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        Post post = new Post();
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setAuthor(user);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(postMapper.toEntity(any(PostDto.class))).thenReturn(post);
        doNothing().when(postService).createPost(any(Post.class));

        mockMvc.perform(post("/post/new")
                        .param("title", "Test Title")
                        .param("content", "Test Content")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("successMessage", "Post successfully created"));

        verify(postService).createPost(any(Post.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testCreatePostValidationErrors() throws Exception {
        mockMvc.perform(post("/post/new")
                        .param("title", "")
                        .param("content", "Short")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("postForm"))
                .andExpect(model().attributeHasFieldErrors("postDto", "title", "content"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testShowUpdateFormPostFound() throws Exception {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Title");
        post.setContent("Test Content");

        when(postService.getPostById(1L)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(new PostDto(1L, "Test Title", "Test Content"));

        mockMvc.perform(get("/post/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("postEditForm"))
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
                .andExpect(flash().attribute("errorMessage", "Post not found"));
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

        mockMvc.perform(post("/post/1/edit")
                        .param("title", "Updated Title")
                        .param("content", "Updated Content")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/post/1"))
                .andExpect(flash().attribute("successMessage", "Post successfully updated"));
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

        mockMvc.perform(post("/post/1/edit")
                        .param("title", "Updated Title")
                        .param("content", "Updated Content")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("errorMessage", "You don't have permission to perform this action"));
    }

    @Test
    @WithMockUser(username = "testuser")
    public void testUpdatePostValidationErrors() throws Exception {
        mockMvc.perform(post("/post/1/edit")
                        .param("title", "")
                        .param("content", "Short")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("postEditForm"))
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
                .andExpect(flash().attribute("successMessage", "Post successfully deleted"));
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
                .andExpect(flash().attribute("errorMessage", "You don't have permission to perform this action"));
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
    }
}