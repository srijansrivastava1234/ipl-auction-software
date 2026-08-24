package com.ipl.auction.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BidRequest {

    @NotNull(message = "Auction ID is mandatory")
    private Long auctionId;

    @NotNull(message = "Player ID is mandatory")
    private Long playerId;

    @NotNull(message = "Team ID is mandatory")
    private Long teamId;

    /**
     * Optional: If null or 0, the bidding engine calculates the next valid incremental bid.
     * If provided, the engine validates that the amount matches or exceeds the required increment.
     */
    private Long bidAmount;
}
