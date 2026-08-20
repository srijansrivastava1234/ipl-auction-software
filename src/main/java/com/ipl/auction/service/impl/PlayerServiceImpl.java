package com.ipl.auction.service.impl;

import com.ipl.auction.dto.request.PlayerPurchaseRequestDto;
import com.ipl.auction.dto.request.PlayerRequestDto;
import com.ipl.auction.dto.response.PlayerResponseDto;
import com.ipl.auction.exception.BadRequestException;
import com.ipl.auction.exception.InsufficientPurseException;
import com.ipl.auction.exception.ResourceNotFoundException;
import com.ipl.auction.exception.SquadLimitExceededException;
import com.ipl.auction.model.Player;
import com.ipl.auction.model.Team;
import com.ipl.auction.repository.PlayerRepository;
import com.ipl.auction.repository.TeamRepository;
import com.ipl.auction.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    @Override
    public PlayerResponseDto createPlayer(PlayerRequestDto requestDto) {
        Player player = Player.builder()
                .name(requestDto.getName())
                .category(requestDto.getCategory().toUpperCase())
                .nationality(requestDto.getNationality().toUpperCase())
                .basePrice(requestDto.getBasePrice())
                .isSold(false)
                .build();

        Player savedPlayer = playerRepository.save(player);
        return mapToResponseDto(savedPlayer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponseDto> getAllPlayers() {
        return playerRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerResponseDto getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with ID: " + id));
        return mapToResponseDto(player);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponseDto> getUnsoldPlayers() {
        return playerRepository.findByIsSold(false)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponseDto> getPlayersByTeam(Long teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new ResourceNotFoundException("Team not found with ID: " + teamId);
        }
        return playerRepository.findByTeamId(teamId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponseDto> getPlayersByCategory(String category) {
        return playerRepository.findByCategoryIgnoreCase(category)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlayerResponseDto updatePlayer(Long id, PlayerRequestDto requestDto) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with ID: " + id));

        player.setName(requestDto.getName());
        player.setCategory(requestDto.getCategory().toUpperCase());
        player.setNationality(requestDto.getNationality().toUpperCase());
        player.setBasePrice(requestDto.getBasePrice());

        Player updatedPlayer = playerRepository.save(player);
        return mapToResponseDto(updatedPlayer);
    }

    @Override
    public void deletePlayer(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with ID: " + id));
        playerRepository.delete(player);
    }

    @Override
    public PlayerResponseDto purchasePlayer(Long playerId, PlayerPurchaseRequestDto purchaseRequest) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with ID: " + playerId));

        if (Boolean.TRUE.equals(player.getIsSold())) {
            throw new BadRequestException("Player '" + player.getName() + "' is already sold to team: " 
                    + (player.getTeam() != null ? player.getTeam().getName() : "Unknown"));
        }

        Team team = teamRepository.findById(purchaseRequest.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + purchaseRequest.getTeamId()));

        // 1. Validate Squad Size Constraint
        int currentSquadSize = team.getPlayers() != null ? team.getPlayers().size() : 0;
        if (currentSquadSize >= team.getMaxSquadSize()) {
            throw new SquadLimitExceededException("Cannot purchase player. Squad size limit of " 
                    + team.getMaxSquadSize() + " reached for team " + team.getShortName());
        }

        // 1b. Validate Overseas Player Cap Constraint (Maximum 8 overseas players allowed per squad)
        if ("OVERSEAS".equalsIgnoreCase(player.getNationality())) {
            long overseasCount = team.getPlayers().stream()
                    .filter(p -> "OVERSEAS".equalsIgnoreCase(p.getNationality()))
                    .count();
            if (overseasCount >= 8) {
                throw new BadRequestException("Cannot purchase player. Team " + team.getShortName() 
                        + " has already reached the maximum limit of 8 overseas players.");
            }
        }

        // 2. Validate Base Price vs Purchase Price Constraint
        BigDecimal purchasePrice = purchaseRequest.getPurchasePrice();
        if (purchasePrice.compareTo(player.getBasePrice()) < 0) {
            throw new BadRequestException("Purchase price (" + purchasePrice + ") cannot be less than base price (" + player.getBasePrice() + ")");
        }

        // 3. Validate Purse Constraint
        if (purchasePrice.compareTo(team.getRemainingPurse()) > 0) {
            throw new InsufficientPurseException("Insufficient purse balance for " + team.getShortName() 
                    + ". Available: ₹" + team.getRemainingPurse() + ", Required: ₹" + purchasePrice);
        }

        // Deduct purse balance & assign player
        team.setRemainingPurse(team.getRemainingPurse().subtract(purchasePrice));
        teamRepository.save(team);

        player.setTeam(team);
        player.setSoldPrice(purchasePrice);
        player.setIsSold(true);

        Player purchasedPlayer = playerRepository.save(player);
        return mapToResponseDto(purchasedPlayer);
    }

    private PlayerResponseDto mapToResponseDto(Player player) {
        return PlayerResponseDto.builder()
                .id(player.getId())
                .name(player.getName())
                .category(player.getCategory())
                .nationality(player.getNationality())
                .basePrice(player.getBasePrice())
                .soldPrice(player.getSoldPrice())
                .isSold(player.getIsSold())
                .teamId(player.getTeam() != null ? player.getTeam().getId() : null)
                .teamName(player.getTeam() != null ? player.getTeam().getName() : null)
                .build();
    }
}
