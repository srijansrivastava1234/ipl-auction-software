package com.ipl.auction.controller;

import com.ipl.auction.dto.request.LoginRequest;
import com.ipl.auction.dto.request.RegisterRequest;
import com.ipl.auction.dto.response.ApiResponse;
import com.ipl.auction.dto.response.AuthResponse;
import com.ipl.auction.dto.response.UserProfileResponse;
import com.ipl.auction.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for User Registration, Authentication, and Profile retrieval.
 * Base Path: /api/v1/auth
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new system user (ADMIN or TEAM_OWNER).
     *
     * @param request registration request payload
     * @return ApiResponse containing AuthResponse with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for username: {}", request.getUsername());
        AuthResponse authResponse = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(authResponse, "User registered successfully"));
    }

    /**
     * Authenticates user credentials and returns JWT token.
     *
     * @param request login request payload
     * @return ApiResponse containing AuthResponse with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for: {}", request.getUsernameOrEmail());
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity
                .ok(ApiResponse.success(authResponse, "User authenticated successfully"));
    }

    /**
     * Retrieves current authenticated user's profile.
     *
     * @param userDetails authenticated user principal
     * @return ApiResponse containing UserProfileResponse
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Fetching profile for current authenticated user: {}", userDetails.getUsername());
        UserProfileResponse profile = authService.getCurrentUserProfile(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(profile, "User profile fetched successfully"));
    }
}
