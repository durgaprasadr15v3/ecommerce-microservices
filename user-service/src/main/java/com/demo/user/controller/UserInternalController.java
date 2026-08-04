package com.demo.user.controller;

import com.demo.user.dto.UserDtos;
import com.demo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

/**
 * Internal endpoints — only called by other microservices, NOT exposed to clients.
 * The API Gateway routes /api/users/internal/** only to internal network (not public).
 */
@RestController
@RequestMapping("/api/users/internal")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserService userService;

    /**
     * Called by order-service and payment-service via WebClient.
     * Example: GET http://user-service/api/users/internal/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDtos.UserResponse> getUserById(@PathVariable long id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }
}
