package com.iwkms.personalBlog.service;

import com.iwkms.personalBlog.model.entity.Post;
import com.iwkms.personalBlog.model.entity.User;
import com.iwkms.personalBlog.repository.CategoryRepository;
import com.iwkms.personalBlog.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.iwkms.personalBlog.config.AppConstants.Roles.ROLE_ADMIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private PostService postService;

    private Post post;
    private User author;
    private User other;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        other = new User();
        other.setId(2L);
        
        post = new Post();
        post.setId(1L);
        post.setTitle("Заголовок");
        post.setContent("Контент");
        post.setAuthor(author);
        post.setCategories(new HashSet<>());
    }

    @Test
    void getAllPosts_shouldReturnList() {
        when(postRepository.findAll()).thenReturn(List.of(post));
        List<Post> result = postService.getAllPosts();
        assertThat(result).hasSize(1).containsExactly(post);
        verify(postRepository).findAll();
    }
    
    @Test
    void getAllPostsPageable_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> page = new PageImpl<>(List.of(post));
        when(postRepository.findAll(pageable)).thenReturn(page);
        
        Page<Post> result = postService.getAllPosts(pageable);
        
        assertThat(result.getContent()).hasSize(1).containsExactly(post);
        verify(postRepository).findAll(pageable);
    }

    @Test
    void getPostById_existingId_shouldReturnOptional() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        Optional<Post> result = postService.getPostById(1L);
        assertThat(result).contains(post);
        verify(postRepository).findById(1L);
    }

    @Test
    void createPost_nullAuthor_shouldThrow() {
        Post p = new Post();
        assertThatThrownBy(() -> postService.createPost(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Author cannot be null");
    }

    @Test
    void updatePost_authorizedUser_shouldUpdate() {
        Post updated = new Post();
        updated.setTitle("Новый");
        updated.setContent("Контент");
        
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Optional<Post> result = postService.updatePost(1L, updated, author);
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Новый");
        verify(postRepository).save(post);
    }

    @Test
    void updatePost_unauthorizedUser_shouldThrow() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        assertThatThrownBy(() -> postService.updatePost(1L, post, other))
                .isInstanceOf(PostService.UnauthorizedAccessException.class);
    }

    @Test
    void deletePost_asAdmin_shouldDelete() {
        User admin = new User();
        admin.setRoles(Set.of(ROLE_ADMIN));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        postService.deletePost(1L, admin);
        verify(postRepository).deleteById(1L);
    }

    @Test
    void deletePost_notFound_shouldThrow() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> postService.deletePost(1L, author))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }
}
