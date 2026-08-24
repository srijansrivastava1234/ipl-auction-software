package com.ipl.auction.dto.response;

import com.ipl.auction.entity.enums.PlayerRole;
import com.ipl.auction.entity.enums.PlayerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentBidView {

    private Long playerId;
    private String fullName;
    private PlayerRole role;
    private String country;
    private boolean overseas;
    private Long basePrice;
    private String formattedBasePrice;
    private Long currentBidPrice;
    private String formattedCurrentBidPrice;
    private Long nextMinimumBid;
    private String formattedNextMinimumBid;
    private PlayerStatus status;
    private Long winningTeamId;
    private String winningTeamName;
    private String winningTeamCode;
    private Long totalBidsPlaced;
    private LocalDateTime lastBidTime;
}
