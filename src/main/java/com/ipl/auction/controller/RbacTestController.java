package com.ipl.auction.controller;

import com.ipl.auction.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller for validating Role-Based Access Control (RBAC) permissions.
 * Base Path: /api/v1/test
 */
@RestController
@RequestMapping("/api/v1/test")
public class RbacTestController {

    /**
     * Public endpoint accessible to all without authentication.
     */
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Map<String, String>>> publicAccess() {
        return ResponseEntity.ok(
                ApiResponse.success(Map.of("access", "PUBLIC"), "Public Content: Accessible by anyone without authentication.")
        );
    }

    /**
     * Endpoint restricted to ADMIN role only.
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> adminAccess() {
        return ResponseEntity.ok(
                ApiResponse.success(Map.of("access", "ROLE_ADMIN"), "Admin Board: Protected endpoint accessible ONLY by users with ROLE_ADMIN.")
        );
    }

    /**
     * Endpoint restricted to TEAM_OWNER role only.
     */
    @GetMapping("/owner")
    @PreAuthorize("hasRole('TEAM_OWNER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> teamOwnerAccess() {
        return ResponseEntity.ok(
                ApiResponse.success(Map.of("access", "ROLE_TEAM_OWNER"), "Team Owner Board: Protected endpoint accessible ONLY by users with ROLE_TEAM_OWNER.")
        );
    }

    /**
     * Endpoint accessible by both ADMIN and TEAM_OWNER roles.
     */
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEAM_OWNER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> authenticatedUserAccess() {
        return ResponseEntity.ok(
                ApiResponse.success(Map.of("access", "AUTHENTICATED"), "Authenticated Board: Accessible by any authenticated user (ADMIN or TEAM_OWNER).")
        );
    }
}
