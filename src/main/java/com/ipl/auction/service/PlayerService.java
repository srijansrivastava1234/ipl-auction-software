package com.ipl.auction.service;

import com.ipl.auction.dto.request.PlayerPurchaseRequestDto;
import com.ipl.auction.dto.request.PlayerRequestDto;
import com.ipl.auction.dto.response.PlayerResponseDto;

import java.util.List;

public interface PlayerService {
    PlayerResponseDto createPlayer(PlayerRequestDto requestDto);
    List<PlayerResponseDto> getAllPlayers();
    PlayerResponseDto getPlayerById(Long id);
    List<PlayerResponseDto> getUnsoldPlayers();
    List<PlayerResponseDto> getPlayersByTeam(Long teamId);
    List<PlayerResponseDto> getPlayersByCategory(String category);
    PlayerResponseDto updatePlayer(Long id, PlayerRequestDto requestDto);
    void deletePlayer(Long id);
    PlayerResponseDto purchasePlayer(Long playerId, PlayerPurchaseRequestDto purchaseRequest);
}
