package com.ipl.auction.repository;

import com.ipl.auction.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByName(String name);
    boolean existsByShortName(String shortName);
    Optional<Team> findByShortName(String shortName);
}
