package com.wish.board.controller;

import com.wish.board.domain.Comment;
import com.wish.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public List<Comment> getCommentsByParam(@RequestParam("postId") Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @PostMapping
    public Map<String, Object> addComment(@RequestParam Long postId,
                                          @RequestParam String content,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        Comment comment = commentService.addComment(postId, content, userDetails.getUsername());

        // 날짜 포맷 지정
        String formattedDate = comment.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 응답을 Map으로 구성
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("id", comment.getId());
        response.put("username", comment.getUsername());
        response.put("content", comment.getContent());
        response.put("createdAt", formattedDate); // 🔽 포맷된 날짜 문자열로 반환

        return response;
    }

    @PutMapping("/{id}")
    public Comment updateComment(@PathVariable Long id,
                                 @RequestParam String content,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return commentService.updateComment(id, content, userDetails.getUsername(), isAdmin);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id,
                              @AuthenticationPrincipal UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        commentService.deleteComment(id, userDetails.getUsername(), isAdmin);
    }
}
