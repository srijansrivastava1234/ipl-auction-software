package com.ipl.auction.repository;

import com.ipl.auction.entity.WalletAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletAuditLogRepository extends JpaRepository<WalletAuditLog, Long> {

    List<WalletAuditLog> findByTeamIdOrderByCreatedAtDesc(Long teamId);
}
