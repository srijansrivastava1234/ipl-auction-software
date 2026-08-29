package com.ipl.auction.service;

import com.ipl.auction.dto.request.TeamRequest;
import com.ipl.auction.dto.response.TeamResponse;
import com.ipl.auction.entity.Team;
import com.ipl.auction.exception.InvalidBidException;
import com.ipl.auction.exception.ResourceNotFoundException;
import com.ipl.auction.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private final TeamRepository teamRepository;

    @Transactional
    public TeamResponse createTeam(TeamRequest request) {
        log.info("Creating new IPL franchise: {}", request.getTeamName());

        if (teamRepository.existsByTeamName(request.getTeamName())) {
            throw new InvalidBidException("A franchise with name '" + request.getTeamName() + "' already exists.");
        }
        if (teamRepository.existsByShortCode(request.getShortCode().toUpperCase())) {
            throw new InvalidBidException("A franchise with short code '" + request.getShortCode() + "' already exists.");
        }

        Team team = Team.builder()
                .teamName(request.getTeamName().trim())
                .shortCode(request.getShortCode().trim().toUpperCase())
                .logoUrl(request.getLogoUrl())
                .totalPurse(request.getTotalPurse())
                .remainingPurse(request.getTotalPurse())
                .maxSquadSize(request.getMaxSquadSize() != null ? request.getMaxSquadSize() : 25)
                .minSquadSize(request.getMinSquadSize() != null ? request.getMinSquadSize() : 18)
                .maxForeignPlayers(request.getMaxForeignPlayers() != null ? request.getMaxForeignPlayers() : 8)
                .currentSquadCount(0)
                .currentForeignCount(0)
                .build();

        Team saved = teamRepository.save(team);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeamResponse getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));
        return mapToResponse(team);
    }

    @Transactional
    public TeamResponse updateTeam(Long id, TeamRequest request) {
        log.info("Updating IPL franchise ID: {}", id);
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));

        if (!team.getTeamName().equalsIgnoreCase(request.getTeamName()) &&
                teamRepository.existsByTeamName(request.getTeamName())) {
            throw new InvalidBidException("A franchise with name '" + request.getTeamName() + "' already exists.");
        }

        if (!team.getShortCode().equalsIgnoreCase(request.getShortCode()) &&
                teamRepository.existsByShortCode(request.getShortCode().toUpperCase())) {
            throw new InvalidBidException("A franchise with short code '" + request.getShortCode() + "' already exists.");
        }

        long purseDifference = request.getTotalPurse() - team.getTotalPurse();
        team.setTeamName(request.getTeamName().trim());
        team.setShortCode(request.getShortCode().trim().toUpperCase());
        team.setLogoUrl(request.getLogoUrl());
        team.setTotalPurse(request.getTotalPurse());
        team.setRemainingPurse(Math.max(0L, team.getRemainingPurse() + purseDifference));
        if (request.getMaxSquadSize() != null) team.setMaxSquadSize(request.getMaxSquadSize());
        if (request.getMinSquadSize() != null) team.setMinSquadSize(request.getMinSquadSize());
        if (request.getMaxForeignPlayers() != null) team.setMaxForeignPlayers(request.getMaxForeignPlayers());

        Team updated = teamRepository.save(team);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteTeam(Long id) {
        log.info("Deleting IPL franchise ID: {}", id);
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + id));
        teamRepository.delete(team);
    }

    public TeamResponse mapToResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .teamName(team.getTeamName())
                .shortCode(team.getShortCode())
                .logoUrl(team.getLogoUrl())
                .totalPurse(team.getTotalPurse())
                .remainingPurse(team.getRemainingPurse())
                .currentSquadCount(team.getCurrentSquadCount())
                .currentForeignCount(team.getCurrentForeignCount())
                .maxSquadSize(team.getMaxSquadSize())
                .minSquadSize(team.getMinSquadSize())
                .maxForeignPlayers(team.getMaxForeignPlayers())
                .build();
    }
}
