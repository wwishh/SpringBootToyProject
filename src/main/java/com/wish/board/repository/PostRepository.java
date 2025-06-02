package com.wish.board.repository;

import com.wish.board.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    long countByAuthor(String author);
    Page<Post> findAll(Pageable pageable);
    Page<Post> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Post> findByContentContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Post> findByAuthorContainingIgnoreCase(String keyword, Pageable pageable);

}