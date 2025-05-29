package com.wish.board.repository;

import com.wish.board.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    long countByAuthor(String author);
}