package com.ipl.auction.repository;

import com.ipl.auction.entity.Bid;
import com.ipl.auction.entity.enums.BidStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    Optional<Bid> findTopByPlayerIdOrderByBidAmountDesc(Long playerId);

    Optional<Bid> findTopByAuctionIdAndPlayerIdOrderByBidAmountDesc(Long auctionId, Long playerId);

    List<Bid> findByPlayerIdOrderByBidTimestampDesc(Long playerId, Pageable pageable);

    List<Bid> findByPlayerIdOrderByBidAmountDesc(Long playerId);

    List<Bid> findByTeamIdOrderByBidTimestampDesc(Long teamId);

    long countByAuctionIdAndPlayerId(Long auctionId, Long playerId);

    @Modifying
    @Query("UPDATE Bid b SET b.bidStatus = :newStatus WHERE b.player.id = :playerId AND b.bidStatus = :oldStatus")
    int updatePreviousBidsStatus(
            @Param("playerId") Long playerId,
            @Param("oldStatus") BidStatus oldStatus,
            @Param("newStatus") BidStatus newStatus
    );
}
