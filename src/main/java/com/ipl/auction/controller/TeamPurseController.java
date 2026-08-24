package com.ipl.auction.controller;

import com.ipl.auction.dto.response.ApiResponse;
import com.ipl.auction.dto.response.TeamPurseSummary;
import com.ipl.auction.service.TeamPurseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
@Tag(name = "Team Purse & Quotas", description = "Real-time franchise wallet, squad capacity, and overseas quotas")
@CrossOrigin(origins = "*")
public class TeamPurseController {

    private final TeamPurseService teamPurseService;

    @GetMapping("/{teamId}/purse-summary")
    @Operation(summary = "Get Franchise Purse Summary", description = "Fetches remaining purse balance, foreign slots left, and max spendable calculation.")
    public ResponseEntity<ApiResponse<TeamPurseSummary>> getTeamPurseSummary(@PathVariable Long teamId) {
        TeamPurseSummary summary = teamPurseService.getTeamPurseSummary(teamId);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/purse-summary")
    @Operation(summary = "Get All Franchises Purse Summaries", description = "Fetches real-time financial standing and squad slots for all 10 IPL teams.")
    public ResponseEntity<ApiResponse<List<TeamPurseSummary>>> getAllTeamsPurseSummary() {
        List<TeamPurseSummary> list = teamPurseService.getAllTeamsPurseSummary();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
