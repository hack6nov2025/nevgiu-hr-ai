package com.example.financeai.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Simple auth response returning JWT and user info.
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String firstName;
    private String lastName;
    private String userId;
}
