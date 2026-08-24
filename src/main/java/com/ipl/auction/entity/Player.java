package com.ipl.auction.entity;

import com.ipl.auction.entity.enums.PlayerRole;
import com.ipl.auction.entity.enums.PlayerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "players", indexes = {
        @Index(name = "idx_players_status", columnList = "status"),
        @Index(name = "idx_players_category", columnList = "auction_set_category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private PlayerRole role;

    @Column(name = "country", nullable = false, length = 60)
    private String country = "India";

    @Column(name = "is_overseas", nullable = false)
    private Boolean isOverseas = false;

    @Column(name = "age")
    private Integer age;

    @NotNull
    @Min(1000000)
    @Column(name = "base_price", nullable = false)
    private Long basePrice; // In INR

    @Column(name = "current_bid_price")
    private Long currentBidPrice = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_winning_team_id")
    private Team currentWinningTeam;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PlayerStatus status = PlayerStatus.AVAILABLE;

    @Column(name = "auction_set_category", length = 50)
    private String auctionSetCategory;

    @Column(name = "photo_url")
    private String photoUrl;

    // Helper state checkers
    public boolean isAvailable() {
        return this.status == PlayerStatus.AVAILABLE;
    }

    public boolean isInAuction() {
        return this.status == PlayerStatus.IN_AUCTION;
    }

    public boolean isSold() {
        return this.status == PlayerStatus.SOLD;
    }
}
