package com.iwkms.personalBlog.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import static com.iwkms.personalBlog.config.AppConstants.Content.POST_CONTENT_MAX_LENGTH;
import static com.iwkms.personalBlog.config.AppConstants.Content.POST_TITLE_MAX_LENGTH;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = POST_TITLE_MAX_LENGTH)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT", length = POST_CONTENT_MAX_LENGTH)
    @Basic(fetch = FetchType.LAZY)
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
}