package com.ipl.auction.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamResponse {
    private Long id;
    private String teamName;
    private String shortCode;
    private String logoUrl;
    private Long totalPurse;
    private Long remainingPurse;
    private Integer currentSquadCount;
    private Integer currentForeignCount;
    private Integer maxSquadSize;
    private Integer minSquadSize;
    private Integer maxForeignPlayers;
}
