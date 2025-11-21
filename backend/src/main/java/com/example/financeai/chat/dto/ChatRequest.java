package com.example.financeai.chat.dto;

import lombok.Data;

/**
 * Chat request payload.
 */
@Data
public class ChatRequest {
    private Long conversationId;
    private String message;
}
