package com.ipl.auction.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ipl.auction.model.Team;
import com.ipl.auction.repository.TeamRepository;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }
}