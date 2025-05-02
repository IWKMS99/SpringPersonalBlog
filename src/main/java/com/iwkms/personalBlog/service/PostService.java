package com.iwkms.personalBlog.service;
import com.iwkms.personalBlog.model.Post;
import com.iwkms.personalBlog.model.User;
import com.iwkms.personalBlog.repository.PostRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private static final Logger log = LogManager.getLogger(PostService.class);
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
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
    public Post createPost(Post post) {
        if (post.getAuthor() == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }
        return postRepository.save(post);
    }

    @Transactional
    public Optional<Post> updatePost(Long id, Post postDetails, User currentUser) {
        return postRepository.findById(id)
                .map(existingPost -> {
                    if (!isAuthorizedToModify(existingPost, currentUser)) {
                        throw new UnauthorizedAccessException("You are not authorized to update this post");
                    }
                    existingPost.setTitle(postDetails.getTitle());
                    existingPost.setContent(postDetails.getContent());
                    return postRepository.save(existingPost);
                });
    }

    @Transactional
    public void deletePost(Long id, User currentUser) {
        Optional<Post> postToDelete = postRepository.findById(id);
        if (postToDelete.isEmpty()) {
            throw new RuntimeException("Post with id " + id + " not found");
        }
        
        if (!isAuthorizedToModify(postToDelete.get(), currentUser)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this post");
        }
        
        postRepository.deleteById(id);
    }
    
    private boolean isAuthorizedToModify(Post post, User user) {
        // Admin can modify any post
        if (user.getRoles().contains("ROLE_ADMIN")) {
            return true;
        }
        
        // Users can only modify their own posts
        return post.getAuthor() != null && post.getAuthor().getId().equals(user.getId());
    }
    
    public static class UnauthorizedAccessException extends RuntimeException {
        public UnauthorizedAccessException(String message) {
            super(message);
        }
    }
}