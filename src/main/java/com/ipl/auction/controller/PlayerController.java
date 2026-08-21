package com.ipl.auction.controller;

import com.ipl.auction.dto.request.PlayerPurchaseRequestDto;
import com.ipl.auction.dto.request.PlayerRequestDto;
import com.ipl.auction.dto.response.ApiResponse;
import com.ipl.auction.dto.response.PlayerResponseDto;
import com.ipl.auction.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<ApiResponse<PlayerResponseDto>> createPlayer(@Valid @RequestBody PlayerRequestDto requestDto) {
        PlayerResponseDto response = playerService.createPlayer(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Player added to auction pool successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlayerResponseDto>>> getAllPlayers() {
        List<PlayerResponseDto> players = playerService.getAllPlayers();
        return ResponseEntity.ok(ApiResponse.success("Players retrieved successfully", players));
    }

    @GetMapping("/unsold")
    public ResponseEntity<ApiResponse<List<PlayerResponseDto>>> getUnsoldPlayers() {
        List<PlayerResponseDto> players = playerService.getUnsoldPlayers();
        return ResponseEntity.ok(ApiResponse.success("Unsold players retrieved successfully", players));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<ApiResponse<List<PlayerResponseDto>>> getPlayersByTeam(@PathVariable Long teamId) {
        List<PlayerResponseDto> players = playerService.getPlayersByTeam(teamId);
        return ResponseEntity.ok(ApiResponse.success("Team players retrieved successfully", players));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<PlayerResponseDto>>> getPlayersByCategory(@PathVariable String category) {
        List<PlayerResponseDto> players = playerService.getPlayersByCategory(category);
        return ResponseEntity.ok(ApiResponse.success("Category players retrieved successfully", players));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlayerResponseDto>> getPlayerById(@PathVariable Long id) {
        PlayerResponseDto player = playerService.getPlayerById(id);
        return ResponseEntity.ok(ApiResponse.success("Player retrieved successfully", player));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlayerResponseDto>> updatePlayer(
            @PathVariable Long id,
            @Valid @RequestBody PlayerRequestDto requestDto) {
        PlayerResponseDto updatedPlayer = playerService.updatePlayer(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Player updated successfully", updatedPlayer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.ok(ApiResponse.success("Player deleted successfully", "Player ID: " + id));
    }

    @PostMapping("/{id}/buy")
    public ResponseEntity<ApiResponse<PlayerResponseDto>> purchasePlayer(
            @PathVariable Long id,
            @Valid @RequestBody PlayerPurchaseRequestDto purchaseRequest) {
        PlayerResponseDto response = playerService.purchasePlayer(id, purchaseRequest);
        return ResponseEntity.ok(ApiResponse.success("Player purchased successfully and added to team squad", response));
    }
}
