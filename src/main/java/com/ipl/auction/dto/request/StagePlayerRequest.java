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
public class StagePlayerRequest {

    @NotNull(message = "Auction ID is mandatory")
    private Long auctionId;

    @NotNull(message = "Player ID is mandatory")
    private Long playerId;
}
