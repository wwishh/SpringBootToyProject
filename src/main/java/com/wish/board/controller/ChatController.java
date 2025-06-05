package com.wish.board.controller;


import com.wish.board.domain.ChatMessage;
import com.wish.board.domain.User;
import com.wish.board.repository.UserRepository;
import com.wish.board.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;

    // WebSocket 메시지 처리
    @MessageMapping("/chat.send/{roomId}")
    public void sendMessage(@DestinationVariable String roomId,
                            @Payload ChatMessage message) {

        message.setRoomId(roomId);
        message.setTimestamp(LocalDateTime.now());

        ChatMessage savedMessage = chatMessageService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, savedMessage);
    }

    // 사용자 목록 보여주기 (HTTP GET /chat)
    @GetMapping
    public String chatUserList(Model model, Principal principal) {
        String currentUsername = principal.getName();
        List<User> users = userRepository.findAll().stream()
                .filter(user -> !user.getUsername().equals(currentUsername))
                .collect(Collectors.toList());

        model.addAttribute("users", users);
        return "chat/userList";
    }

    // 채팅방 진입 (HTTP GET /chat/{receiver})
    @GetMapping("/{receiver}")
    public String enterChat(@PathVariable String receiver, Principal principal, Model model) {
        String sender = principal.getName();
        String roomId = generateRoomId(sender, receiver);

        // 🔥 이전 채팅 메시지 불러오기
        List<ChatMessage> chatHistory = chatMessageService.getMessagesByRoomId(roomId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("sender", sender);
        model.addAttribute("receiver", receiver);
        model.addAttribute("chatHistory", chatHistory);
        return "chat/chat";
    }

    private String generateRoomId(String user1, String user2) {
        return Stream.of(user1, user2)
                .sorted()
                .collect(Collectors.joining("_"));
    }

    @GetMapping("/history/{roomId}")
    @ResponseBody
    public List<ChatMessage> getChatHistory(@PathVariable String roomId) {
        return chatMessageService.findByRoomId(roomId);
    }
}


