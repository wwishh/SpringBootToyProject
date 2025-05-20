package com.wish.board.controller;

import com.wish.board.domain.Comment;
import com.wish.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{postId}")
    public List<Comment> getComments(@PathVariable("postId") Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @PostMapping
    public Comment addComment(@RequestParam Long postId,
                              @RequestParam String content,
                              @AuthenticationPrincipal UserDetails userDetails) {
        return commentService.addComment(postId, content, userDetails.getUsername());
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
