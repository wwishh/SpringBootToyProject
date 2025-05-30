package com.wish.board.repository;

import com.wish.board.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    long countByAuthor(String author);
    List<Post> findByTitleContainingIgnoreCase(String keyword);
    List<Post> findByContentContainingIgnoreCase(String keyword);
    List<Post> findByAuthorContainingIgnoreCase(String keyword);

}