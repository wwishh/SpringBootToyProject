package com.wish.board.controller;

import com.wish.board.domain.Post;
import com.wish.board.service.PostService;
import com.wish.board.service.CommentService;
import com.wish.board.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    @GetMapping
    public String list(Model model) {
        List<Post> posts = postService.getAllPosts();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Map<String, Object>> formattedPosts = posts.stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("author", post.getAuthor());
            map.put("createdAt", post.getCreatedAt() != null ? post.getCreatedAt().format(formatter) : "");

            // 댓글 수 추가
            int commentCount = commentService.getCommentsByPostId(post.getId()).size();
            map.put("commentCount", commentCount);

            return map;
        }).collect(Collectors.toList());

        model.addAttribute("posts", formattedPosts);
        return "post/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("formAction", "/posts");
        model.addAttribute("isEdit", false);
        return "post/form";
    }

    @PostMapping
    public String create(@ModelAttribute Post post, @AuthenticationPrincipal UserDetails userDetails) {
        post.setAuthor(userDetails.getUsername());
        postService.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Post post = postService.findById(id);
        String formattedDate = post.getCreatedAt() != null ? post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";

        List<Comment> comments = commentService.getCommentsByPostId(id);

        // 댓글 개수 추가
        int commentCount = comments.size();

        // 각 댓글에 formattedDate 속성 추가 (속성을 Map으로 전달)
        List<Map<String, Object>> formattedComments = comments.stream().map(comment -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", comment.getId());
            map.put("username", comment.getUsername());
            map.put("content", comment.getContent());
            map.put("formattedDate", comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return map;
        }).collect(Collectors.toList());

        model.addAttribute("post", post);
        model.addAttribute("formattedDate", formattedDate);
        model.addAttribute("comments", formattedComments);  // 여기를 Map 리스트로 교체
        model.addAttribute("commentCount", commentCount);  // 댓글 개수 추가
        return "post/detail";
    }


    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Post post = postService.findById(id);
        model.addAttribute("post", post);
        model.addAttribute("formAction", "/posts/" + id + "/edit");
        model.addAttribute("isEdit", true);
        return "post/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute Post post) {
        post.setId(id);

        // 기존 작성자 정보가 없으면 DB에서 가져와서 설정
        if (post.getAuthor() == null || post.getAuthor().isBlank()) {
            Post existingPost = postService.findById(id);
            post.setAuthor(existingPost.getAuthor());
        }

        postService.save(post);
        return "redirect:/posts";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication authentication) {
        postService.delete(id, authentication);
        return "redirect:/posts";
    }

}
