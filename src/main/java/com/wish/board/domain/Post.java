package com.wish.board.domain;

import lombok.Data;
import jakarta.persistence.*; // ✅ 중요 포인트
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String author;

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

//    // ✅ 게시글 삭제 시 댓글도 함께 삭제되도록 설정
//    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
//    private List<Comment> comments = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}