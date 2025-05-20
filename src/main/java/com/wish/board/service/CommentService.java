package com.wish.board.service;

import com.wish.board.domain.Comment;
import com.wish.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    public Comment addComment(Long postId, String content, String username) {
        Comment comment = Comment.builder()
                .postId(postId)
                .content(content)
                .username(username)
                .createdAt(LocalDateTime.now())
                .build();
        return commentRepository.save(comment);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public Comment updateComment(Long id, String content, String username, boolean isAdmin) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        if (!comment.getUsername().equals(username) && !isAdmin) {
            throw new SecurityException("댓글을 수정할 권한이 없습니다.");
        }
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id, String username, boolean isAdmin) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        if (!comment.getUsername().equals(username) && !isAdmin) {
            throw new SecurityException("댓글을 삭제할 권한이 없습니다.");
        }
        commentRepository.delete(comment);
    }
}
