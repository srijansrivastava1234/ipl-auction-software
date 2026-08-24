package com.ipl.auction.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "team_name", nullable = false, unique = true, length = 100)
    private String teamName;

    @NotBlank
    @Column(name = "short_code", nullable = false, unique = true, length = 10)
    private String shortCode;

    @Column(name = "logo_url")
    private String logoUrl;

    @NotNull
    @Min(0)
    @Column(name = "total_purse", nullable = false)
    private Long totalPurse = 1000000000L; // ₹100 Crores in INR

    @NotNull
    @Min(0)
    @Column(name = "remaining_purse", nullable = false)
    private Long remainingPurse = 1000000000L;

    @Column(name = "max_squad_size", nullable = false)
    private Integer maxSquadSize = 25;

    @Column(name = "min_squad_size", nullable = false)
    private Integer minSquadSize = 18;

    @Column(name = "max_foreign_players", nullable = false)
    private Integer maxForeignPlayers = 8;

    @Column(name = "current_squad_count", nullable = false)
    private Integer currentSquadCount = 0;

    @Column(name = "current_foreign_count", nullable = false)
    private Integer currentForeignCount = 0;

    @JsonIgnore
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TeamSquad> squadMembers = new ArrayList<>();

    // Domain Business Logic Helper Methods

    public boolean hasSufficientPurse(Long bidAmount) {
        return this.remainingPurse != null && this.remainingPurse >= bidAmount;
    }

    public boolean canAddPlayer() {
        return this.currentSquadCount < this.maxSquadSize;
    }

    public boolean canAddOverseasPlayer() {
        return this.currentForeignCount < this.maxForeignPlayers;
    }

    /**
     * Calculates the minimum purse reserve needed to fill up the squad to the mandatory minimum of 18 players.
     * Minimum base price in IPL is ₹20 Lakhs (2,000,000 INR).
     */
    public Long calculateRequiredPurseReserve(Long potentialBidAmount) {
        int slotsNeededForMin = Math.max(0, this.minSquadSize - (this.currentSquadCount + 1));
        long minPlayerBasePrice = 2000000L; // ₹20 Lakhs
        return slotsNeededForMin * minPlayerBasePrice;
    }

    public void deductPurse(Long amount) {
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Deduction amount cannot be negative or null");
        }
        if (this.remainingPurse < amount) {
            throw new IllegalStateException("Insufficient purse balance to deduct: " + amount);
        }
        this.remainingPurse -= amount;
    }
}
