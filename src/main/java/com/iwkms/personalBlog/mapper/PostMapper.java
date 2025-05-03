package com.iwkms.personalBlog.mapper;

import com.iwkms.personalBlog.dto.PostDto;
import com.iwkms.personalBlog.model.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    public PostDto toDto(Post post) {
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent()
        );
    }

    public Post toEntity(PostDto dto) {
        Post post = new Post();
        post.setId(dto.getId());
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        return post;
    }
}
