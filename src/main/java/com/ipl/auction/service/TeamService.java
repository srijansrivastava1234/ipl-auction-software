package com.ipl.auction.service;

import com.ipl.auction.dto.request.TeamRequestDto;
import com.ipl.auction.dto.response.TeamResponseDto;

import java.util.List;

public interface TeamService {
    TeamResponseDto createTeam(TeamRequestDto requestDto);
    List<TeamResponseDto> getAllTeams();
    TeamResponseDto getTeamById(Long id);
    TeamResponseDto getTeamByShortName(String shortName);
    TeamResponseDto updateTeam(Long id, TeamRequestDto requestDto);
    void deleteTeam(Long id);
}
