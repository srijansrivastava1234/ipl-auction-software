package com.ipl.auction.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ipl.auction.model.Player;
import com.ipl.auction.model.Player.PlayerStatus;
import com.ipl.auction.model.Team;
import com.ipl.auction.repository.PlayerRepository;
import com.ipl.auction.repository.TeamRepository;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public PlayerService(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Player sellPlayer(Long playerId, Long teamId, BigDecimal finalPrice) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        BigDecimal teamBudget = team.getBudget() != null ? team.getBudget() : BigDecimal.ZERO;

        if (teamBudget.compareTo(finalPrice) < 0) {
            throw new RuntimeException("Team does not have sufficient budget!");
        }

        // Deduct budget
        team.setBudget(teamBudget.subtract(finalPrice));
        teamRepository.save(team);

        // Assign player to team & mark SOLD using enum
        player.setTeam(team);
        player.setStatus(PlayerStatus.SOLD);
        player.setBasePrice(finalPrice);

        return playerRepository.save(player);
    }
}