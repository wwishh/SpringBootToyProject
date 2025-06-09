package com.wish.board.repository;

import com.wish.board.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);
    List<ChatMessage> findByRoomIdAndReceiverAndReadIsFalse(String roomId, String receiver);

    @Query("SELECT c.sender, COUNT(c) FROM ChatMessage c WHERE c.receiver = :currentUser AND c.read = false GROUP BY c.sender")
    List<Object[]> countUnreadMessagesGroupBySender(@Param("currentUser") String currentUser);

}
