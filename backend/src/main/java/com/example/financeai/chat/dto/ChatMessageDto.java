package com.example.financeai.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

/**
 * Chat message DTO for frontend.
 */
@Data
@AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    private String sender;
    private String content;
    private Instant createdAt;
}
