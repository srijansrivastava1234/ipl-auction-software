package com.ipl.auction.service;

import com.ipl.auction.dto.response.BidResponse;
import com.ipl.auction.dto.response.TeamPurseSummary;
import com.ipl.auction.entity.Team;
import com.ipl.auction.exception.ResourceNotFoundException;
import com.ipl.auction.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamPurseService {

    private final TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public TeamPurseSummary getTeamPurseSummary(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + teamId));

        Long spentPurse = team.getTotalPurse() - team.getRemainingPurse();
        int slotsRemaining = team.getMaxSquadSize() - team.getCurrentSquadCount();
        int foreignSlotsRemaining = team.getMaxForeignPlayers() - team.getCurrentForeignCount();

        // Calculate minimum reserve required for remaining slots to hit 18 players
        Long minPurseReserve = team.calculateRequiredPurseReserve(0L);

        // Maximum that can safely be spent on a single player without violating the reserve
        Long maxSpendable = Math.max(0, team.getRemainingPurse() - minPurseReserve);

        return TeamPurseSummary.builder()
                .teamId(team.getId())
                .teamName(team.getTeamName())
                .shortCode(team.getShortCode())
                .totalPurse(team.getTotalPurse())
                .formattedTotalPurse(BidResponse.formatCurrency(team.getTotalPurse()))
                .remainingPurse(team.getRemainingPurse())
                .formattedRemainingPurse(BidResponse.formatCurrency(team.getRemainingPurse()))
                .spentPurse(spentPurse)
                .formattedSpentPurse(BidResponse.formatCurrency(spentPurse))
                .currentSquadCount(team.getCurrentSquadCount())
                .maxSquadSize(team.getMaxSquadSize())
                .slotsRemaining(slotsRemaining)
                .currentForeignCount(team.getCurrentForeignCount())
                .maxForeignPlayers(team.getMaxForeignPlayers())
                .foreignSlotsRemaining(foreignSlotsRemaining)
                .minimumPurseReserve(minPurseReserve)
                .formattedMinimumPurseReserve(BidResponse.formatCurrency(minPurseReserve))
                .maxSpendableOnSinglePlayer(maxSpendable)
                .formattedMaxSpendable(BidResponse.formatCurrency(maxSpendable))
                .build();
    }

    @Transactional(readOnly = true)
    public List<TeamPurseSummary> getAllTeamsPurseSummary() {
        return teamRepository.findAll().stream()
                .map(team -> getTeamPurseSummary(team.getId()))
                .collect(Collectors.toList());
    }
}
