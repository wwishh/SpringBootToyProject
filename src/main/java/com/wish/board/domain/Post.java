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

    private String imagePath; // 예: /uploads/image123.png


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}