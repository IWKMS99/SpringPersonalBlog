package com.iwkms.personalBlog.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "post_categories",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();
}