package com.ipl.auction.controller;

import com.ipl.auction.dto.request.PlayerRequest;
import com.ipl.auction.dto.response.ApiResponse;
import com.ipl.auction.dto.response.PlayerResponse;
import com.ipl.auction.entity.enums.PlayerStatus;
import com.ipl.auction.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<ApiResponse<PlayerResponse>> createPlayer(@Valid @RequestBody PlayerRequest request) {
        PlayerResponse created = playerService.createPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Player registered to auction pool successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PlayerResponse>>> getAllPlayers(
            @RequestParam(required = false) PlayerStatus status,
            @RequestParam(required = false) String category) {
        List<PlayerResponse> players = playerService.getAllPlayers(status, category);
        return ResponseEntity.ok(ApiResponse.success(players, "Fetched players from pool"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlayerResponse>> getPlayerById(@PathVariable Long id) {
        PlayerResponse player = playerService.getPlayerById(id);
        return ResponseEntity.ok(ApiResponse.success(player, "Fetched player details"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PlayerResponse>> updatePlayer(@PathVariable Long id, @Valid @RequestBody PlayerRequest request) {
        PlayerResponse updated = playerService.updatePlayer(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated, "Player details updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Player removed from auction pool"));
    }
}
