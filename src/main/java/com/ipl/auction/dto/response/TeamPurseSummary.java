package com.ipl.auction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamPurseSummary {

    private Long teamId;
    private String teamName;
    private String shortCode;
    private Long totalPurse;
    private String formattedTotalPurse;
    private Long remainingPurse;
    private String formattedRemainingPurse;
    private Long spentPurse;
    private String formattedSpentPurse;
    private Integer currentSquadCount;
    private Integer maxSquadSize;
    private Integer slotsRemaining;
    private Integer currentForeignCount;
    private Integer maxForeignPlayers;
    private Integer foreignSlotsRemaining;
    private Long minimumPurseReserve;
    private String formattedMinimumPurseReserve;
    private Long maxSpendableOnSinglePlayer;
    private String formattedMaxSpendable;
}
