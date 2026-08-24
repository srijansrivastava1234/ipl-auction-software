package com.ipl.auction.dto.response;

import com.ipl.auction.entity.enums.PlayerRole;
import com.ipl.auction.entity.enums.PlayerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerAuctionSummary {

    private Long playerId;
    private String fullName;
    private PlayerRole role;
    private String country;
    private boolean overseas;
    private Long basePrice;
    private String formattedBasePrice;
    private Long finalSoldPrice;
    private String formattedFinalPrice;
    private PlayerStatus status;
    private String soldToTeamName;
    private String soldToTeamCode;
}
