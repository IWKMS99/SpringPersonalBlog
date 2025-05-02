package com.iwkms.personalBlog.controller;
import com.iwkms.personalBlog.model.Post;
import com.iwkms.personalBlog.model.User;
import com.iwkms.personalBlog.repository.UserRepository;
import com.iwkms.personalBlog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
public class PostController {
    // View names
    private static final String REDIRECT_HOME = "redirect:/";
    private static final String REDIRECT_POST_DETAIL = "redirect:/post/%d";
    private static final String VIEW_POSTS = "posts";
    private static final String VIEW_POST_DETAILS = "postDetails";
    private static final String VIEW_POST_FORM = "postForm";
    private static final String VIEW_POST_EDIT_FORM = "postEditForm";
    
    // Model attributes
    private static final String ATTR_ERROR_MESSAGE = "errorMessage";
    private static final String ATTR_SUCCESS_MESSAGE = "successMessage";
    private static final String ATTR_POST = "post";
    private static final String ATTR_POSTS = "posts";
    
    // Messages
    private static final String MSG_POST_NOT_FOUND = "Post not found";
    private static final String MSG_POST_CREATED = "Post successfully created";
    private static final String MSG_POST_UPDATED = "Post successfully updated";
    private static final String MSG_POST_DELETED = "Post successfully deleted";
    private static final String MSG_USER_NOT_FOUND = "User not found";
    
    private final PostService postService;
    private final UserRepository userRepository;
    
    @Autowired
    public PostController(PostService postService, UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
    }
    
    @GetMapping("/")
    public String listPosts(Model model) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute(ATTR_POSTS, posts);
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
        model.addAttribute(ATTR_POST, new Post());
        return VIEW_POST_FORM;
    }
    
    @PostMapping("/post/new")
    public String createPost(@ModelAttribute Post post, Authentication authentication, 
                             RedirectAttributes redirectAttributes) {
        try {
            User author = getCurrentUser(authentication);
            post.setAuthor(author);
            postService.createPost(post);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, MSG_POST_CREATED);
            return REDIRECT_HOME;
        } catch (RuntimeException e) {
            return handleError(redirectAttributes, e.getMessage());
        }
    }
    
    @GetMapping("/post/{id}/edit")
    public String showUpdateForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return postService.getPostById(id)
                .map(post -> {
                    model.addAttribute(ATTR_POST, post);
                    return VIEW_POST_EDIT_FORM;
                })
                .orElseGet(() -> handlePostNotFound(redirectAttributes));
    }
    
    @PostMapping("/post/{id}/edit")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post postDetails, 
                             RedirectAttributes redirectAttributes) {
        return postService.updatePost(id, postDetails)
                .map(post -> {
                    redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, MSG_POST_UPDATED);
                    return String.format(REDIRECT_POST_DETAIL, id);
                })
                .orElseGet(() -> handlePostNotFound(redirectAttributes));
    }
    
    @PostMapping("/post/{id}/delete")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            postService.deletePost(id);
            redirectAttributes.addFlashAttribute(ATTR_SUCCESS_MESSAGE, MSG_POST_DELETED);
        } catch (RuntimeException e) {
            return handleError(redirectAttributes, e.getMessage());
        }
        return REDIRECT_HOME;
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }


    // Extract method for handling not found cases
    private String handlePostNotFound(RedirectAttributes redirectAttributes) {
        return handleError(redirectAttributes, MSG_POST_NOT_FOUND);
    }
    
    // Extract method for handling general errors
    private String handleError(RedirectAttributes redirectAttributes, String errorMessage) {
        redirectAttributes.addFlashAttribute(ATTR_ERROR_MESSAGE, errorMessage);
        return REDIRECT_HOME;
    }
    
    // Extract method for getting current user
    private User getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(MSG_USER_NOT_FOUND));
    }
}