package com.ipl.auction.controller;

import com.ipl.auction.dto.request.StagePlayerRequest;
import com.ipl.auction.dto.response.ApiResponse;
import com.ipl.auction.dto.response.PlayerAuctionSummary;
import com.ipl.auction.service.AuctioneerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auction")
@RequiredArgsConstructor
@Tag(name = "Auctioneer Operations", description = "Endpoints for stage transitions, hammer strikes, and unsold player passes")
@CrossOrigin(origins = "*")
public class AuctioneerController {

    private final AuctioneerService auctioneerService;

    @PostMapping("/stage")
    @Operation(summary = "Bring Player to Auction Stage", description = "Transitions an available player to IN_AUCTION status.")
    public ResponseEntity<ApiResponse<PlayerAuctionSummary>> stagePlayer(@Valid @RequestBody StagePlayerRequest request) {
        PlayerAuctionSummary summary = auctioneerService.bringPlayerToStage(request.getAuctionId(), request.getPlayerId());
        return ResponseEntity.ok(ApiResponse.success(summary, "Player " + summary.getFullName() + " is now on the auction stage!"));
    }

    @PostMapping("/players/{playerId}/hammer/sold")
    @Operation(summary = "Hammer Strike: SOLD", description = "Finalizes the sale of the player to the highest bidder, deducting team purse and updating squad.")
    public ResponseEntity<ApiResponse<PlayerAuctionSummary>> strikeSold(
            @PathVariable Long playerId,
            @RequestParam(defaultValue = "1") Long auctionId) {
        PlayerAuctionSummary summary = auctioneerService.strikeHammerSold(auctionId, playerId);
        return ResponseEntity.ok(ApiResponse.success(summary, "SOLD! Player " + summary.getFullName() + " acquired by " + summary.getSoldToTeamName()));
    }

    @PostMapping("/players/{playerId}/hammer/unsold")
    @Operation(summary = "Pass Player: UNSOLD", description = "Marks a player as UNSOLD when no bids are placed.")
    public ResponseEntity<ApiResponse<PlayerAuctionSummary>> strikeUnsold(
            @PathVariable Long playerId,
            @RequestParam(defaultValue = "1") Long auctionId) {
        PlayerAuctionSummary summary = auctioneerService.passPlayerUnsold(auctionId, playerId);
        return ResponseEntity.ok(ApiResponse.success(summary, "Player " + summary.getFullName() + " passed as UNSOLD."));
    }
}
