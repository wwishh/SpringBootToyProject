package com.wish.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.wish.board.repository")  // repository 패키지 경로 추가
@EntityScan(basePackages = "com.wish.board.domain") // 엔티티 클래스 위치 지정
public class WishApplication {

	public static void main(String[] args) {
		SpringApplication.run(WishApplication.class, args);
	}

}
