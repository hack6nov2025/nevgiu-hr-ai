package com.example.financeai.document;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Document service for RAG using Spring AI PgVectorStore.
 */
@Service
public class DocumentService {

    private final VectorStore vectorStore;

    public DocumentService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void storeTextForUser(String ownerUserId, String text) {
        List<String> chunks = simpleChunk(text, 700);

        List<Document> documents = new ArrayList<>();

        for (String chunk : chunks) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("ownerUserId", ownerUserId);
            metadata.put("source", "user-upload");

            Document doc = new Document(chunk, metadata);
            documents.add(doc);
        }

        vectorStore.add(documents);
    }

    List<String> simpleChunk(String text, int maxSize) {
        List<String> result = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return result;
        }

        int length = text.length();
        int start = 0;

        while (start < length) {
            int end = Math.min(start + maxSize, length);
            result.add(text.substring(start, end));
            start = end;
        }

        return result;
    }

}
