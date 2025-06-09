package com.wish.board.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatRoomSummaryDto {
    private String roomId;
    private String opponentUsername;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private int unreadCount;
}
