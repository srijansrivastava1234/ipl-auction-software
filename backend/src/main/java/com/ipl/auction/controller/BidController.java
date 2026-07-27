package com.ipl.auction.controller;

import com.ipl.auction.model.Bid;
import com.ipl.auction.service.BidService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/bids")
@CrossOrigin(origins = "*")
public class BidController {

    private final BidService bidService;
    private final SimpMessagingTemplate messagingTemplate;

    public BidController(BidService bidService, SimpMessagingTemplate messagingTemplate) {
        this.bidService = bidService;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    public ResponseEntity<?> placeBid(@RequestBody Map<String, Object> payload) {
        try {
            Long playerId = Long.parseLong(payload.get("playerId").toString());
            Long teamId = Long.parseLong(payload.get("teamId").toString());
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());

            Bid bid = bidService.placeBid(playerId, teamId, amount);

            // Broadcast real-time update to all connected team devices
            messagingTemplate.convertAndSend("/topic/bids", bid);

            return ResponseEntity.ok(bid);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}