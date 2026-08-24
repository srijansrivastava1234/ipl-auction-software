package com.ipl.auction.repository;

import com.ipl.auction.entity.TeamSquad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamSquadRepository extends JpaRepository<TeamSquad, Long> {

    List<TeamSquad> findByTeamId(Long teamId);

    Optional<TeamSquad> findByPlayerId(Long playerId);

    boolean existsByPlayerId(Long playerId);

    long countByTeamId(Long teamId);
}
