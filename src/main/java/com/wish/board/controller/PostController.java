package com.wish.board.controller;

import com.wish.board.domain.Post;
import com.wish.board.service.PostService;
import com.wish.board.service.CommentService;
import com.wish.board.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    @GetMapping
    public String list(@RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "searchType", required = false, defaultValue = "title") String searchType,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       Model model) {

        int size = 10; // 한 페이지당 게시글 수
        Page<Post> postPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            postPage = postService.searchPosts(keyword.trim(), searchType, page, size);
        } else {
            postPage = postService.getPagedPosts(page, size);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Map<String, Object>> formattedPosts = postPage.stream().map(post -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("author", post.getAuthor());
            map.put("createdAt", post.getCreatedAt() != null ? post.getCreatedAt().format(formatter) : "");
            map.put("hasImage", post.getImagePath() != null && !post.getImagePath().isBlank());
            map.put("viewCount", post.getViewCount()); // ✅ 조회수 포함
            int commentCount = commentService.getCommentsByPostId(post.getId()).size();
            map.put("commentCount", commentCount);
            return map;
        }).collect(Collectors.toList());

        model.addAttribute("posts", formattedPosts);
        model.addAttribute("currentPage", postPage.getNumber());
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        return "post/list";
    }


    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("formAction", "/posts");
        model.addAttribute("isEdit", false);
        return "post/form";
    }

    // 글 작성
    @PostMapping
    public String create(@ModelAttribute Post post,
                         @AuthenticationPrincipal UserDetails userDetails,
                         @RequestParam("imageFile") MultipartFile imageFile) {
        post.setAuthor(userDetails.getUsername());

        handleImageUpload(post, imageFile);

        postService.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Post post = postService.findByIdAndIncreaseView(id); // ✅ 조회수 증가되는 메서드 사용
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

        // 기존 이미지 경로 추가
        model.addAttribute("existingImagePath", post.getImagePath());

        return "post/form";
    }

    // 글 수정 (기존 이미지 유지 반영)
    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") Long id,
                         @ModelAttribute Post post,
                         @RequestParam("imageFile") MultipartFile imageFile,
                         @RequestParam(value = "existingImagePath", required = false) String existingImagePath) {

        post.setId(id);

        // 기존 작성자 유지
        if (post.getAuthor() == null || post.getAuthor().isBlank()) {
            Post existingPost = postService.findById(id);
            post.setAuthor(existingPost.getAuthor());
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            handleImageUpload(post, imageFile);
        } else {
            // 새 이미지 없으면 기존 이미지 경로 유지
            post.setImagePath(existingImagePath);
        }

        postService.save(post);
        return "redirect:/posts";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication authentication) {
        postService.delete(id, authentication);
        return "redirect:/posts";
    }

    // 이미지 업로드 처리 메서드
    private void handleImageUpload(Post post, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String originalFileName = imageFile.getOriginalFilename();
                if (originalFileName == null) {
                    throw new IllegalArgumentException("이미지 파일 이름이 null입니다.");
                }

                String fileName = StringUtils.cleanPath(originalFileName);
                String uploadDir = System.getProperty("user.dir") + "/uploads/";

                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) {
                    boolean created = uploadPath.mkdirs();
                    if (!created) {
                        throw new IOException("업로드 디렉토리를 생성하지 못했습니다.");
                    }
                }

                String uuid = UUID.randomUUID().toString();
                String savedFileName = uuid + "_" + fileName;
                String filePath = uploadDir + savedFileName;
                File dest = new File(filePath);
                imageFile.transferTo(dest);

                // 웹 접근용 경로: /uploads/파일명 으로만 저장
                post.setImagePath("/uploads/" + savedFileName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}





