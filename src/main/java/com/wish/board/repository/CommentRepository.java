package com.wish.board.repository;

import com.wish.board.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 기존 서비스 코드를 위해 post.id로 변경
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    @Transactional
    void deleteByPostId(Long postId);
}
