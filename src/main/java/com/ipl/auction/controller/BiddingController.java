package com.ipl.auction.controller;

import com.ipl.auction.dto.request.BidRequest;
import com.ipl.auction.dto.response.ApiResponse;
import com.ipl.auction.dto.response.BidResponse;
import com.ipl.auction.dto.response.CurrentBidView;
import com.ipl.auction.service.BiddingEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bids")
@RequiredArgsConstructor
@Tag(name = "Live Bidding Engine", description = "High-throughput live bidding REST endpoints with Concurrency Control")
@CrossOrigin(origins = "*")
public class BiddingController {

    private final BiddingEngineService biddingEngineService;

    @PostMapping("/place")
    @Operation(summary = "Place a Live Bid", description = "Places an atomic bid with Pessimistic Row Locking to prevent race conditions.")
    public ResponseEntity<ApiResponse<BidResponse>> placeBid(@Valid @RequestBody BidRequest request) {
        BidResponse response = biddingEngineService.placeBid(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Bid placed successfully!"));
    }

    @GetMapping("/player/{playerId}/current")
    @Operation(summary = "Get Current Bidding State", description = "Fetches the current live bid, winning team, and next minimum bid for a player.")
    public ResponseEntity<ApiResponse<CurrentBidView>> getCurrentBiddingState(@PathVariable Long playerId) {
        CurrentBidView view = biddingEngineService.getCurrentBiddingState(playerId);
        return ResponseEntity.ok(ApiResponse.success(view));
    }

    @GetMapping("/player/{playerId}/history")
    @Operation(summary = "Get Player Bid History", description = "Retrieves the audit trail of all bids placed on a player.")
    public ResponseEntity<ApiResponse<List<BidResponse>>> getPlayerBidHistory(
            @PathVariable Long playerId,
            @RequestParam(defaultValue = "50") int limit) {
        List<BidResponse> history = biddingEngineService.getPlayerBidHistory(playerId, limit);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
