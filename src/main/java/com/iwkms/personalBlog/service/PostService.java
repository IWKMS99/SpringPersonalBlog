package com.iwkms.personalBlog.service;

import com.iwkms.personalBlog.model.entity.Post;
import com.iwkms.personalBlog.model.entity.User;
import com.iwkms.personalBlog.repository.CategoryRepository;
import com.iwkms.personalBlog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.iwkms.personalBlog.config.AppConstants.Roles.ROLE_ADMIN;
import static com.iwkms.personalBlog.config.AppConstants.Security.SAFE_HTML_POLICY;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    private String sanitize(String raw) {
        return raw != null ? SAFE_HTML_POLICY.sanitize(raw) : null;
    }

    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Post> getPostsByCategory(Long categoryId, Pageable pageable) {
        return categoryRepository.findById(categoryId)
                .map(category -> postRepository.findByCategory(category, pageable))
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
    }
    
    @Transactional(readOnly = true)
    public Page<Post> getPostsByAuthor(User author, Pageable pageable) {
        return postRepository.findByAuthor(author, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Post> searchPosts(String keyword, Pageable pageable) {
        return postRepository.searchByKeyword(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public Post createPost(Post post) {
        if (post.getAuthor() == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }
        post.setContent(sanitize(post.getContent()));
        return postRepository.save(post);
    }

    @Transactional
    public Optional<Post> updatePost(Long id, Post postDetails, User currentUser) {
        return postRepository.findById(id)
                .map(existingPost -> {
                    if (isNotAuthorizedToModify(existingPost, currentUser)) {
                        throw new UnauthorizedAccessException("You are not authorized to update this post");
                    }
                    existingPost.setTitle(postDetails.getTitle());
                    existingPost.setContent(sanitize(postDetails.getContent()));
                    
                    // Обновляем категории, если они указаны
                    if (postDetails.getCategories() != null) {
                        existingPost.setCategories(postDetails.getCategories());
                    }
                    
                    return postRepository.save(existingPost);
                });
    }

    @Transactional
    public void deletePost(Long id, User currentUser) {
        Optional<Post> postToDelete = postRepository.findById(id);
        if (postToDelete.isEmpty()) {
            throw new RuntimeException("Post with id " + id + " not found");
        }

        if (isNotAuthorizedToModify(postToDelete.get(), currentUser)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this post");
        }

        postRepository.deleteById(id);
    }

    private boolean isNotAuthorizedToModify(Post post, User user) {
        if (user.getRoles().contains(ROLE_ADMIN)) {
            return false;
        }

        boolean isAuthor = post.getAuthor() != null && post.getAuthor().getId().equals(user.getId());
        return !isAuthor;
    }

    public static class UnauthorizedAccessException extends RuntimeException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }
}