package com.ipl.auction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponseDto {
    private Long id;
    private String name;
    private String shortName;
    private BigDecimal totalPurse;
    private BigDecimal remainingPurse;
    private Integer maxSquadSize;
    private Integer currentSquadCount;
}
