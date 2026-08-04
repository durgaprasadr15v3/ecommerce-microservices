package com.demo.user.service;

import com.demo.user.dto.UserDtos;
import com.demo.user.dto.UserResponse;
import com.demo.user.model.User;
import com.demo.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import com.demo.user.event.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PublishRegistrationEvent publishRegistrationEvent;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    public UserDtos.AuthResponse register(UserDtos.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .role(User.Role.USER)
                .build();
        User saved = userRepository.save(user);

        // sending user registration message
        publishRegistrationEvent.userNotification(user.getEmail());
        String token = generateToken(saved);

        return new UserDtos.AuthResponse(token, saved.getId(),
                saved.getName(), saved.getEmail(), saved.getRole().name());
    }

    public UserDtos.AuthResponse login(UserDtos.LoginRequest request) {

        System.out.println(request.getEmail() + "  " + request.getPassword());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = generateToken(user);
        return new UserDtos.AuthResponse(token, user.getId(),
                user.getName(), user.getEmail(), user.getRole().name());
    }

    public UserDtos.UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        UserDtos.UserResponse response = new UserDtos.UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole().name());
        return response;
    }

    private String generateToken(User user) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());

        String compact = Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        return compact;
    }

    public List<UserResponse> getAllUserDetails() {
      // List<User> allUsers= userRepository.findAll();
       return userRepository.findAll().stream().map(this::convertToDto).toList();
    }

    private UserResponse convertToDto(User user) {

        UserResponse dto = new UserResponse();

        dto.setId(String.valueOf(user.getId()));
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());

        return dto;
    }

//    public Optional< UserDtos.UserResponse> getuserById(Long userId) {
//        User UserResponse=userRepository.findById(userId);
//
//        return ;
//
//    }
}
