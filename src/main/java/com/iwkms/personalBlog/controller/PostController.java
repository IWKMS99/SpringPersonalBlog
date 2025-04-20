package com.iwkms.personalBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.iwkms.personalBlog.model.Post;
import com.iwkms.personalBlog.service.PostService;
import java.util.List;
import java.util.Optional;

@Controller
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String listPosts(Model model) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);

        return "posts";
    }

    @GetMapping("/post/{id}")
    public String showPost(@PathVariable Long id, Model model) {
        Optional<Post> postOptional = postService.getPostById(id);
        if (postOptional.isPresent()) {
            model.addAttribute("post", postOptional.get());
            return "post-details";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/post/new")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        return "post-form";
    }

    @PostMapping("/post/new")
    public String createPost(@ModelAttribute Post post) {
        postService.createPost(post);
        return "redirect:/";
    }
}
