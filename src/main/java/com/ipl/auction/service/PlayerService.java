package com.ipl.auction.service;

import com.ipl.auction.dto.request.PlayerRequest;
import com.ipl.auction.dto.response.PlayerResponse;
import com.ipl.auction.entity.Player;
import com.ipl.auction.entity.enums.PlayerRole;
import com.ipl.auction.entity.enums.PlayerStatus;
import com.ipl.auction.exception.InvalidBidException;
import com.ipl.auction.exception.ResourceNotFoundException;
import com.ipl.auction.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Transactional
    public PlayerResponse createPlayer(PlayerRequest request) {
        log.info("Registering new player for auction pool: {}", request.getFullName());

        Player player = Player.builder()
                .fullName(request.getFullName().trim())
                .role(request.getRole())
                .country(request.getCountry().trim())
                .isOverseas(request.getIsOverseas() != null ? request.getIsOverseas() : !"India".equalsIgnoreCase(request.getCountry().trim()))
                .age(request.getAge())
                .basePrice(request.getBasePrice())
                .currentBidPrice(0L)
                .status(PlayerStatus.AVAILABLE)
                .auctionSetCategory(request.getAuctionSetCategory())
                .photoUrl(request.getPhotoUrl())
                .build();

        Player saved = playerRepository.save(player);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> getAllPlayers(PlayerStatus status, String category) {
        List<Player> players;
        if (status != null) {
            players = playerRepository.findByStatus(status);
        } else if (category != null && !category.isBlank()) {
            players = playerRepository.findByAuctionSetCategory(category);
        } else {
            players = playerRepository.findAll();
        }
        return players.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlayerResponse getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with ID: " + id));
        return mapToResponse(player);
    }

    @Transactional
    public PlayerResponse updatePlayer(Long id, PlayerRequest request) {
        log.info("Updating player details ID: {}", id);
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with ID: " + id));

        if (player.isInAuction() || player.isSold()) {
            throw new InvalidBidException("Cannot update player details while in auction or already sold.");
        }

        player.setFullName(request.getFullName().trim());
        player.setRole(request.getRole());
        player.setCountry(request.getCountry().trim());
        player.setIsOverseas(request.getIsOverseas() != null ? request.getIsOverseas() : !"India".equalsIgnoreCase(request.getCountry().trim()));
        player.setAge(request.getAge());
        player.setBasePrice(request.getBasePrice());
        player.setAuctionSetCategory(request.getAuctionSetCategory());
        player.setPhotoUrl(request.getPhotoUrl());

        Player updated = playerRepository.save(player);
        return mapToResponse(updated);
    }

    @Transactional
    public void deletePlayer(Long id) {
        log.info("Deleting player from pool ID: {}", id);
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with ID: " + id));
        if (player.isInAuction() || player.isSold()) {
            throw new InvalidBidException("Cannot delete player who is currently in auction or sold to a franchise.");
        }
        playerRepository.delete(player);
    }

    public PlayerResponse mapToResponse(Player player) {
        return PlayerResponse.builder()
                .id(player.getId())
                .fullName(player.getFullName())
                .role(player.getRole())
                .country(player.getCountry())
                .isOverseas(player.getIsOverseas())
                .age(player.getAge())
                .basePrice(player.getBasePrice())
                .currentBidPrice(player.getCurrentBidPrice())
                .status(player.getStatus())
                .auctionSetCategory(player.getAuctionSetCategory())
                .photoUrl(player.getPhotoUrl())
                .currentWinningTeamId(player.getCurrentWinningTeam() != null ? player.getCurrentWinningTeam().getId() : null)
                .currentWinningTeamName(player.getCurrentWinningTeam() != null ? player.getCurrentWinningTeam().getTeamName() : null)
                .build();
    }
}
