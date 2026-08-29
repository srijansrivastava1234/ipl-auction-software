package com.ipl.auction.dto.response;

import com.ipl.auction.entity.enums.PlayerRole;
import com.ipl.auction.entity.enums.PlayerStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerResponse {
    private Long id;
    private String fullName;
    private PlayerRole role;
    private String country;
    private Boolean isOverseas;
    private Integer age;
    private Long basePrice;
    private Long currentBidPrice;
    private PlayerStatus status;
    private String auctionSetCategory;
    private String photoUrl;
    private Long currentWinningTeamId;
    private String currentWinningTeamName;
}
