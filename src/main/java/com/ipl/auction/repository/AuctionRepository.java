package com.ipl.auction.repository;

import com.ipl.auction.entity.Auction;
import com.ipl.auction.entity.enums.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {

    Optional<Auction> findFirstByStatus(AuctionStatus status);

    List<Auction> findByYear(Integer year);
}
