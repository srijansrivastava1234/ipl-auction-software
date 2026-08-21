package com.ipl.auction.service.impl;

import com.ipl.auction.dto.request.TeamRequestDto;
import com.ipl.auction.dto.response.TeamResponseDto;
import com.ipl.auction.exception.BadRequestException;
import com.ipl.auction.exception.ResourceNotFoundException;
import com.ipl.auction.model.Team;
import com.ipl.auction.repository.TeamRepository;
import com.ipl.auction.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    @Override
    public TeamResponseDto createTeam(TeamRequestDto requestDto) {
        if (teamRepository.existsByName(requestDto.getName())) {
            throw new BadRequestException("Team with name '" + requestDto.getName() + "' already exists");
        }
        if (teamRepository.existsByShortName(requestDto.getShortName())) {
            throw new BadRequestException("Team with short name '" + requestDto.getShortName() + "' already exists");
        }

        int squadSize = (requestDto.getMaxSquadSize() != null && requestDto.getMaxSquadSize() > 0) 
                ? requestDto.getMaxSquadSize() : 25;

        Team team = Team.builder()
                .name(requestDto.getName())
                .shortName(requestDto.getShortName().toUpperCase())
                .totalPurse(requestDto.getTotalPurse())
                .remainingPurse(requestDto.getTotalPurse())
                .maxSquadSize(squadSize)
                .build();

        Team savedTeam = teamRepository.save(team);
        return mapToResponseDto(savedTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponseDto> getAllTeams() {
        return teamRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponseDto getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));
        return mapToResponseDto(team);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponseDto getTeamByShortName(String shortName) {
        Team team = teamRepository.findByShortName(shortName.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with short name: " + shortName));
        return mapToResponseDto(team);
    }

    @Override
    public TeamResponseDto updateTeam(Long id, TeamRequestDto requestDto) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));

        if (!team.getName().equalsIgnoreCase(requestDto.getName()) && teamRepository.existsByName(requestDto.getName())) {
            throw new BadRequestException("Team with name '" + requestDto.getName() + "' already exists");
        }
        if (!team.getShortName().equalsIgnoreCase(requestDto.getShortName()) && teamRepository.existsByShortName(requestDto.getShortName())) {
            throw new BadRequestException("Team with short name '" + requestDto.getShortName() + "' already exists");
        }

        team.setName(requestDto.getName());
        team.setShortName(requestDto.getShortName().toUpperCase());
        team.setTotalPurse(requestDto.getTotalPurse());
        if (requestDto.getMaxSquadSize() != null && requestDto.getMaxSquadSize() > 0) {
            team.setMaxSquadSize(requestDto.getMaxSquadSize());
        }

        Team updatedTeam = teamRepository.save(team);
        return mapToResponseDto(updatedTeam);
    }

    @Override
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));
        teamRepository.delete(team);
    }

    private TeamResponseDto mapToResponseDto(Team team) {
        int squadCount = team.getPlayers() != null ? team.getPlayers().size() : 0;
        return TeamResponseDto.builder()
                .id(team.getId())
                .name(team.getName())
                .shortName(team.getShortName())
                .totalPurse(team.getTotalPurse())
                .remainingPurse(team.getRemainingPurse())
                .maxSquadSize(team.getMaxSquadSize())
                .currentSquadCount(squadCount)
                .build();
    }
}
