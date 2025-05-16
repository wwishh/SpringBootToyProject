package com.wish.board.service;

import com.wish.board.domain.Post;
import com.wish.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow();
    }


    public List<Post> getAllPosts() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Post update(Long id, Post post) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());
        existingPost.setAuthor(post.getAuthor());
        return postRepository.save(existingPost); // 수정된 게시물 저장
    }

    public void delete(Long id) {
        // 게시글을 찾아서 삭제
        Post post = postRepository.findById(id).orElse(null);

        // 만약 게시글이 존재하지 않으면 예외를 던진다.
        if (post == null) {
            throw new IllegalArgumentException("Post not found with id: " + id);
        }

        postRepository.delete(post);
    }




}