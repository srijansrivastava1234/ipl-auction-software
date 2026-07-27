package com.ipl.auction.controller;

import com.ipl.auction.model.Player;
import com.ipl.auction.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerService playerService;
    private final SimpMessagingTemplate messagingTemplate;

    public PlayerController(PlayerService playerService, SimpMessagingTemplate messagingTemplate) {
        this.playerService = playerService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    @PutMapping("/{id}/sell")
    public ResponseEntity<?> sellPlayer(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Long teamId = Long.parseLong(payload.get("teamId").toString());
            BigDecimal finalPrice = new BigDecimal(payload.get("finalPrice").toString());

            Player soldPlayer = playerService.sellPlayer(id, teamId, finalPrice);

            // Broadcast real-time player sale update to all connected devices
            messagingTemplate.convertAndSend("/topic/players", soldPlayer);

            return ResponseEntity.ok(soldPlayer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}