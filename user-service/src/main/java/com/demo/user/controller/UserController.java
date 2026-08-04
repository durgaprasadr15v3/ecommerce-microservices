package com.demo.user.controller;

import com.demo.user.dto.UserDtos;
import com.demo.user.dto.UserResponse;
import com.demo.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000/")
@RequiredArgsConstructor
public class UserController {

    private final UserService
            userService;

    @PostMapping("/register")
    public ResponseEntity<UserDtos.AuthResponse> register(
            @Valid @RequestBody UserDtos.RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDtos.AuthResponse> login(
            @Valid @RequestBody UserDtos.LoginRequest request) {

        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDtos.UserResponse> getProfile(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "user-service"));
    }

    @GetMapping("/welcome")
    public String welcome()
    {
        return "welcome to user service";
    }

    @GetMapping("/getAllUsers")
    public List<UserResponse> getAllUsers()
    {
        return userService.getAllUserDetails();
    }


//    @GetMapping("/getUserById")
//    public Optional< UserDtos.UserResponse> getUserById(@PathVariable Long userId)
//    {
//        return userService.getuserById(userId);
//    }

}
