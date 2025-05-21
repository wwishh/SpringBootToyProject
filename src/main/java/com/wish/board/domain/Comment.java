package com.wish.board.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private String username;

    @Column(nullable = false)
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;


//    // ✅ 게시글과의 관계
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "post_id", nullable = false)
//    private Post post;

}
