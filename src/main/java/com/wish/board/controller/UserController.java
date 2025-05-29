package com.wish.board.controller;

import com.wish.board.domain.User;
import com.wish.board.repository.PostRepository;
import com.wish.board.repository.UserRepository;
import com.wish.board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "user/signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute User user, Model model) {
        try {
            userService.register(user);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", user);  // 입력 값 유지
            return "user/signup";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/mypage")
    public String myPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        // 사용자 정보 조회
        User user = userRepository.findByUsername(username).orElseThrow();

        // 작성한 게시글 수 조회
        long postCount = postRepository.countByAuthor(username);

        model.addAttribute("user", user);
        model.addAttribute("postCount", postCount);
        return "user/mypage";
    }

    @PostMapping("/mypage")
    public String updateUser(@RequestParam String email,
                             @RequestParam(required = false) String password,
                             @RequestParam(value = "profileImage", required = false) MultipartFile file,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        String currentUsername = userDetails.getUsername();

        try {
            User user = userRepository.findByUsername(currentUsername).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            user.setEmail(email);
            if (password != null && !password.isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
            }

            userService.updateUserWithImage(user, file);

            redirectAttributes.addFlashAttribute("successMessage", "회원 정보가 수정되었습니다.");
            return "redirect:/posts";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            User user = userRepository.findByUsername(currentUsername).orElse(null);
            model.addAttribute("user", user);
            return "user/mypage";
        }
    }



}
