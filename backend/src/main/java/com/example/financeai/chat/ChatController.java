package com.example.financeai.chat;

import com.example.financeai.chat.dto.ChatMessageDto;
import com.example.financeai.chat.dto.ChatRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller exposing chat endpoints.
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessageDto> send(
            @AuthenticationPrincipal(expression = "userId") String userId,
            @RequestBody ChatRequest request) {

        ChatMessageDto response =
                chatService.handleUserMessage(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{conversationId}")
    public ResponseEntity<?> history(
            @AuthenticationPrincipal(expression = "userId") String userId,
            @PathVariable Long conversationId) {

        return ResponseEntity.ok(
                chatService.getConversationHistory(userId, conversationId)
        );
    }

    @GetMapping("/conversations")
    public ResponseEntity<?> conversations(
            @AuthenticationPrincipal(expression = "userId") String userId) {

        return ResponseEntity.ok(
                chatService.listConversations(userId)
        );
    }

}
