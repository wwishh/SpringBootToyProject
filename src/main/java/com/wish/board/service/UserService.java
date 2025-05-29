package com.wish.board.service;

import com.wish.board.domain.User;
import com.wish.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(User user) {
        // 중복 검사
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        // 비밀번호 암호화 후 저장
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void updateUserWithImage(User user, MultipartFile file) {
        String uploadDir = System.getProperty("user.dir") + "/uploads/profile/";

        if (file != null && !file.isEmpty()) {
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

            try {
                file.transferTo(new File(uploadDir + filename));
                user.setProfileImage("/uploads/profile/" + filename);  // 전체 경로 저장
            } catch (IOException e) {
                throw new IllegalArgumentException("이미지 업로드 실패: " + e.getMessage());
            }
        }

        userRepository.save(user);
    }




}
