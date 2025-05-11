package com.iwkms.personalBlog.repository;

import com.iwkms.personalBlog.model.entity.Category;
import com.iwkms.personalBlog.model.entity.Post;
import com.iwkms.personalBlog.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @NonNull
    Page<Post> findAll(@NonNull Pageable pageable);
    
    @Query("SELECT p FROM Post p JOIN p.categories c WHERE c = :category")
    Page<Post> findByCategory(Category category, Pageable pageable);
    
    Page<Post> findByAuthor(User author, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(CAST(p.content AS String)) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchByKeyword(String keyword, Pageable pageable);
}