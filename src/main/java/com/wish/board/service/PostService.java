package com.wish.board.service;

import com.wish.board.domain.Post;
import com.wish.board.repository.CommentRepository;
import com.wish.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;  // 댓글 레포 추가

    // 게시글 저장
    public Post save(Post post) {
        return postRepository.save(post);
    }


    // 최신순 정렬된 전체 게시글 조회
    public List<Post> getAllPosts() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    // ID로 게시글 찾기
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다. ID: " + id));
    }

    // 게시글 수정
    public Post update(Long id, Post post) {
        Post existingPost = findById(id);
        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());
        existingPost.setAuthor(post.getAuthor());
        return postRepository.save(existingPost);
    }

    // 게시글 삭제
    public void delete(Long id, Authentication authentication) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Post not found with id: " + id)
        );

        String currentUsername = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        // 관리자 또는 작성자 본인만 삭제 가능
        if (!post.getAuthor().equals(currentUsername) && !isAdmin) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }

        // 1. 댓글 먼저 삭제
        commentRepository.deleteByPostId(id);

        postRepository.delete(post);
    }

    public Page<Post> getPagedPosts(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")); // 페이지 번호는 0부터 시작, size는 10
        return postRepository.findAll(pageRequest);
    }

    // 검색 포함 버전
    public Page<Post> searchPosts(String keyword, String searchType, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        switch (searchType) {
            case "content":
                return postRepository.findByContentContainingIgnoreCase(keyword, pageRequest);
            case "author":
                return postRepository.findByAuthorContainingIgnoreCase(keyword, pageRequest);
            default:
                return postRepository.findByTitleContainingIgnoreCase(keyword, pageRequest);
        }
    }

    //조히수 증가 메서드
    @Transactional
    public Post findByIdAndIncreaseView(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        post.setViewCount(post.getViewCount() + 1); // 조회수 증가
        return post;
    }


}
