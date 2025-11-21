package com.example.financeai.auth.dto;

import lombok.Data;

/**
 * Registration request DTO.
 */
@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String userId;
    private String password;
}
