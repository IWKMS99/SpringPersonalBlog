package com.iwkms.personalBlog.service;
import com.iwkms.personalBlog.model.Post;
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
        return postRepository.save(post);
    }

    @Transactional
    public Optional<Post> updatePost(Long id, Post postDetails) {
        return postRepository.findById(id)
                .map(postEntity -> {
                    postEntity.setTitle(postDetails.getTitle());
                    postEntity.setContent(postDetails.getContent());
                    return postRepository.save(postEntity);
                });
    }

    @Transactional
    public void deletePost(Long id) {
        try {
            postRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Post with id " + id + " not found");
        }
    }
}