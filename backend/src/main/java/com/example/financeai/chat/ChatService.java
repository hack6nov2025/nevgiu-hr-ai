package com.example.financeai.chat;

import com.example.financeai.chat.dto.ChatMessageDto;
import com.example.financeai.chat.dto.ChatRequest;
import com.example.financeai.model.Conversation;
import com.example.financeai.model.Message;
import com.example.financeai.model.User;
import com.example.financeai.repo.ConversationRepository;
import com.example.financeai.repo.MessageRepository;
import com.example.financeai.repo.UserRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service that orchestrates chat and RAG.
 */
@Service
public class ChatService {

    private final ChatClient chatClient;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final VectorStore vectorStore;

    public ChatService(ChatClient chatClient,
                       UserRepository userRepository,
                       ConversationRepository conversationRepository,
                       MessageRepository messageRepository,
                       VectorStore vectorStore) {

        this.chatClient = chatClient;
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.vectorStore = vectorStore;
    }

    @Transactional
    public ChatMessageDto handleUserMessage(
            String userId,
            ChatRequest request) {

        User user = userRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Conversation conversation;
        if (request.getConversationId() == null) {
            conversation = new Conversation();
            conversation.setUser(user);
            conversation.setTitle("Finance chat " + Instant.now());
            conversationRepository.save(conversation);
        } else {
            conversation = conversationRepository
                    .findById(request.getConversationId())
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));
        }

        Message userMessage = new Message();
        userMessage.setConversation(conversation);
        userMessage.setSender("USER");
        userMessage.setContent(request.getMessage());
        messageRepository.save(userMessage);

        List<Document> contextDocs =
                retrieveContextForUser(userId, request.getMessage());

        String aiAnswer =
                generateFinanceAnswerWithContext(
                        request.getMessage(),
                        contextDocs
                );

        Message assistantMessage = new Message();
        assistantMessage.setConversation(conversation);
        assistantMessage.setSender("ASSISTANT");
        assistantMessage.setContent(aiAnswer);
        messageRepository.save(assistantMessage);

        return new ChatMessageDto(
                assistantMessage.getId(),
                assistantMessage.getSender(),
                assistantMessage.getContent(),
                assistantMessage.getCreatedAt()
        );
    }

    private List<Document> retrieveContextForUser(
            String ownerUserId,
            String query) {

        List<Document> docs = vectorStore.similaritySearch(query, 5);

        return docs.stream()
                .filter(doc -> ownerUserId.equals(
                        doc.getMetadata().get("ownerUserId")))
                .collect(Collectors.toList());
    }

    @Cacheable(
            value = "chatAnswers",
            key = "T(java.util.Objects).hash(#prompt, #contextKey)"
    )
    public String generateFinanceAnswerWithContext(
            String prompt,
            List<Document> contextDocs) {

        String contextTexts = contextDocs.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n\n"));

        String contextKey = Integer.toHexString(
                Objects.hash(contextTexts)
        );

        String systemPrompt =
                "You are a helpful assistant that ONLY answers questions " +
                "about finance and banking. If the user asks about other topics, " +
                "politely refuse and redirect to finance / banking topics.";

        String fullUserPrompt =
                "User question:\n" + prompt + "\n\n" +
                "Context from user's documents (if any):\n" + contextTexts;

        return chatClient
                .prompt()
                .system(systemPrompt)
                .user(fullUserPrompt)
                .call()
                .content();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getConversationHistory(
            String userId,
            Long conversationId) {

        Conversation conversation = conversationRepository
                .findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        return messageRepository
                .findByConversationOrderByCreatedAtAsc(conversation)
                .stream()
                .map(m -> new ChatMessageDto(
                        m.getId(),
                        m.getSender(),
                        m.getContent(),
                        m.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Conversation> listConversations(String userId) {
        User user = userRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return conversationRepository.findByUserOrderByCreatedAtDesc(user);
    }

}
