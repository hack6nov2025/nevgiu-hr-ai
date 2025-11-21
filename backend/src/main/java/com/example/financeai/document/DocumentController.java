package com.example.financeai.document;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Simple document upload endpoint for RAG.
 */
@RestController
@RequestMapping("/api/documents")
@CrossOrigin
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload-text")
    public ResponseEntity<String> uploadText(
            @AuthenticationPrincipal(expression = "userId") String userId,
            @RequestBody String text) {

        documentService.storeTextForUser(userId, text);
        return ResponseEntity.ok("Document stored");
    }

}
