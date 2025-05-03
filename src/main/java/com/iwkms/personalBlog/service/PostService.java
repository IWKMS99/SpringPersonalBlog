package com.iwkms.personalBlog.service;

import com.iwkms.personalBlog.model.entity.Post;
import com.iwkms.personalBlog.model.entity.User;
import com.iwkms.personalBlog.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.iwkms.personalBlog.config.AppConstants.Roles.ROLE_ADMIN;
import static com.iwkms.personalBlog.config.AppConstants.Security.SAFE_HTML_POLICY;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    private String sanitize(String raw) {
        return raw != null ? SAFE_HTML_POLICY.sanitize(raw) : null;
    }

    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public void createPost(Post post) {
        if (post.getAuthor() == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }
        post.setContent(sanitize(post.getContent()));
        postRepository.save(post);
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