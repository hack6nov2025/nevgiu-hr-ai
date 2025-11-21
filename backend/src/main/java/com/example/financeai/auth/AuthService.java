package com.example.financeai.auth;

import com.example.financeai.auth.dto.AuthResponse;
import com.example.financeai.auth.dto.LoginRequest;
import com.example.financeai.auth.dto.RegisterRequest;
import com.example.financeai.model.User;
import com.example.financeai.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication and registration service.
 */
@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUserId(request.getUserId())) {
            throw new RuntimeException("UserId already exists");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUserId(request.getUserId());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());

        return new AuthResponse(
                token,
                user.getFirstName(),
                user.getLastName(),
                user.getUserId()
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository
                .findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getUsername());

        return new AuthResponse(
                token,
                user.getFirstName(),
                user.getLastName(),
                user.getUserId()
        );
    }

    @Override
    public User loadUserByUsername(String username) {
        return userRepository
                .findByUserId(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
