package com.example.financeai.auth.dto;

import lombok.Data;

/**
 * Login request DTO.
 */
@Data
public class LoginRequest {
    private String userId;
    private String password;
}
