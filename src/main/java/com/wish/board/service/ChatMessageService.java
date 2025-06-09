package com.wish.board.service;


import com.wish.board.domain.ChatMessage;
import com.wish.board.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> findByRoomId(String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }

    public List<ChatMessage> getMessagesByRoomId(String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }

    public void markMessagesAsRead(String roomId, String receiver) {
        List<ChatMessage> unreadMessages = chatMessageRepository.findByRoomIdAndReceiverAndReadIsFalse(roomId, receiver);
        for (ChatMessage message : unreadMessages) {
            message.setRead(true);
        }
        chatMessageRepository.saveAll(unreadMessages);
    }

    public Map<String, Integer> getUnreadCountsByUser(String currentUser) {
        List<Object[]> results = chatMessageRepository.countUnreadMessagesGroupBySender(currentUser);
        Map<String, Integer> unreadCounts = new HashMap<>();
        for (Object[] row : results) {
            String sender = (String) row[0];
            Long count = (Long) row[1];
            unreadCounts.put(sender, count.intValue());
        }
        return unreadCounts;
    }



}

