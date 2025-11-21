package com.example.financeai.repo;

import com.example.financeai.model.Conversation;
import com.example.financeai.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Message repository.
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);

}
