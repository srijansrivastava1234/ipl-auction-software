package com.ipl.auction.repository;

import com.ipl.auction.entity.Team;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    /**
     * Pessimistic Write Lock ensures no two threads simultaneously evaluate
     * and deduct the same franchise's purse balance.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Team t WHERE t.id = :id")
    Optional<Team> findByIdWithPessimisticLock(@Param("id") Long id);

    Optional<Team> findByShortCode(String shortCode);

    boolean existsByTeamName(String teamName);

    boolean existsByShortCode(String shortCode);
}
