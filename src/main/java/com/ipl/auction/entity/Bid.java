package com.ipl.auction.entity;

import com.ipl.auction.entity.enums.BidStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "bids", indexes = {
        @Index(name = "idx_bids_player_amount", columnList = "player_id, bid_amount DESC"),
        @Index(name = "idx_bids_auction_player", columnList = "auction_id, player_id"),
        @Index(name = "idx_bids_team", columnList = "team_id"),
        @Index(name = "idx_bids_timestamp", columnList = "bid_timestamp DESC")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @NotNull
    @Min(1)
    @Column(name = "bid_amount", nullable = false)
    private Long bidAmount;

    @CreatedDate
    @Column(name = "bid_timestamp", nullable = false, updatable = false)
    private LocalDateTime bidTimestamp;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "bid_status", nullable = false, length = 30)
    private BidStatus bidStatus = BidStatus.ACCEPTED;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
