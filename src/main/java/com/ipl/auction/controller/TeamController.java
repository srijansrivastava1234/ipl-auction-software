package com.ipl.auction.controller;

import com.ipl.auction.dto.request.TeamRequestDto;
import com.ipl.auction.dto.response.ApiResponse;
import com.ipl.auction.dto.response.TeamResponseDto;
import com.ipl.auction.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<ApiResponse<TeamResponseDto>> createTeam(@Valid @RequestBody TeamRequestDto requestDto) {
        TeamResponseDto response = teamService.createTeam(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Team created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeamResponseDto>>> getAllTeams() {
        List<TeamResponseDto> teams = teamService.getAllTeams();
        return ResponseEntity.ok(ApiResponse.success("Teams retrieved successfully", teams));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeamResponseDto>> getTeamById(@PathVariable Long id) {
        TeamResponseDto team = teamService.getTeamById(id);
        return ResponseEntity.ok(ApiResponse.success("Team retrieved successfully", team));
    }

    @GetMapping("/shortname/{shortName}")
    public ResponseEntity<ApiResponse<TeamResponseDto>> getTeamByShortName(@PathVariable String shortName) {
        TeamResponseDto team = teamService.getTeamByShortName(shortName);
        return ResponseEntity.ok(ApiResponse.success("Team retrieved successfully", team));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TeamResponseDto>> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody TeamRequestDto requestDto) {
        TeamResponseDto updatedTeam = teamService.updateTeam(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Team updated successfully", updatedTeam));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.ok(ApiResponse.success("Team deleted successfully", "Team ID: " + id));
    }
}
