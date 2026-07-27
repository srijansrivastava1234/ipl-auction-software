package com.ipl.auction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ipl.auction.model.Player;
import com.ipl.auction.model.Player.PlayerStatus;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByStatus(PlayerStatus status);
    List<Player> findByRole(String role);
}