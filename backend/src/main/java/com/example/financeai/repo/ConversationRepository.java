package com.example.financeai.repo;

import com.example.financeai.model.Conversation;
import com.example.financeai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Conversation repository.
 */
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByUserOrderByCreatedAtDesc(User user);

}
