package com.ipl.auction.service;

import com.ipl.auction.dto.request.BidRequest;
import com.ipl.auction.dto.response.BidResponse;
import com.ipl.auction.dto.response.CurrentBidView;
import com.ipl.auction.entity.Auction;
import com.ipl.auction.entity.Bid;
import com.ipl.auction.entity.Player;
import com.ipl.auction.entity.Team;
import com.ipl.auction.entity.enums.BidStatus;
import com.ipl.auction.entity.enums.PlayerStatus;
import com.ipl.auction.exception.*;
import com.ipl.auction.repository.AuctionRepository;
import com.ipl.auction.repository.BidRepository;
import com.ipl.auction.repository.PlayerRepository;
import com.ipl.auction.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BiddingEngineService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    /**
     * Executes an atomic bid placement with Pessimistic Row Locks on both Player and Team.
     * Guaranteed thread-safe against concurrent franchise bidding wars.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BidResponse placeBid(BidRequest request) {
        log.info("Processing bid request: Auction={}, Player={}, Team={}, Amount={}",
                request.getAuctionId(), request.getPlayerId(), request.getTeamId(), request.getBidAmount());

        // 1. Validate Auction Event
        Auction auction = auctionRepository.findById(request.getAuctionId())
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with ID: " + request.getAuctionId()));

        if (!auction.isLive()) {
            throw new InvalidBidException("Cannot place bid. Auction '" + auction.getTitle() + "' is currently " + auction.getStatus());
        }

        // 2. Acquire Pessimistic Write Lock on Player
        Player player = playerRepository.findByIdWithPessimisticLock(request.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with ID: " + request.getPlayerId()));

        if (player.getStatus() != PlayerStatus.IN_AUCTION) {
            throw new PlayerNotAvailableException("Player " + player.getFullName() +
                    " is currently " + player.getStatus() + ", not active on the auction stage.");
        }

        // 3. Acquire Pessimistic Write Lock on Team
        Team team = teamRepository.findByIdWithPessimisticLock(request.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with ID: " + request.getTeamId()));

        // 4. Validate Self-Outbidding Rule
        if (player.getCurrentWinningTeam() != null && player.getCurrentWinningTeam().getId().equals(team.getId())) {
            throw new InvalidBidException("Franchise " + team.getShortCode() + " already holds the highest bid (" +
                    BidResponse.formatCurrency(player.getCurrentBidPrice()) + "). You cannot outbid yourself.");
        }

        // 5. Validate Squad Capacity & Foreign Quota
        if (!team.canAddPlayer()) {
            throw new SquadLimitExceededException("Franchise " + team.getShortCode() +
                    " has reached the maximum squad limit of " + team.getMaxSquadSize() + " players.");
        }

        if (Boolean.TRUE.equals(player.getIsOverseas()) && !team.canAddOverseasPlayer()) {
            throw new SquadLimitExceededException("Franchise " + team.getShortCode() +
                    " has reached the maximum overseas player quota of " + team.getMaxForeignPlayers() + " players.");
        }

        // 6. Calculate Next Valid Minimum Bid & Incremental Slab
        Long currentPrice = (player.getCurrentBidPrice() != null && player.getCurrentBidPrice() > 0)
                ? player.getCurrentBidPrice()
                : player.getBasePrice();

        boolean isOpeningBid = (player.getCurrentWinningTeam() == null && (player.getCurrentBidPrice() == null || player.getCurrentBidPrice() == 0));
        Long minNextBid = isOpeningBid ? player.getBasePrice() : calculateNextMinimumBid(currentPrice);

        Long effectiveBidAmount = (request.getBidAmount() != null && request.getBidAmount() > 0)
                ? request.getBidAmount()
                : minNextBid;

        if (effectiveBidAmount < minNextBid) {
            throw new InvalidBidException("Bid amount " + BidResponse.formatCurrency(effectiveBidAmount) +
                    " is invalid. Minimum required bid is " + BidResponse.formatCurrency(minNextBid));
        }

        // 7. Validate Purse Sufficiency & Mandatory Minimum Squad Reserve
        if (!team.hasSufficientPurse(effectiveBidAmount)) {
            throw new InsufficientPurseException("Franchise " + team.getShortCode() +
                    " has insufficient purse balance (" + BidResponse.formatCurrency(team.getRemainingPurse()) +
                    ") to place bid of " + BidResponse.formatCurrency(effectiveBidAmount));
        }

        Long purseAfterBid = team.getRemainingPurse() - effectiveBidAmount;
        Long requiredPurseReserve = team.calculateRequiredPurseReserve(effectiveBidAmount);
        if (purseAfterBid < requiredPurseReserve) {
            throw new InsufficientPurseException("Bid rejected. Franchise must retain at least " +
                    BidResponse.formatCurrency(requiredPurseReserve) + " reserve to complete minimum squad of " +
                    team.getMinSquadSize() + " players. Remaining after this bid would be only " +
                    BidResponse.formatCurrency(purseAfterBid));
        }

        // 8. Atomic State Update: Outbid previous highest bidder
        bidRepository.updatePreviousBidsStatus(player.getId(), BidStatus.ACCEPTED, BidStatus.OUTBID);

        // 9. Persist New Winning Bid
        Bid newBid = Bid.builder()
                .auction(auction)
                .player(player)
                .team(team)
                .bidAmount(effectiveBidAmount)
                .bidStatus(BidStatus.ACCEPTED)
                .bidTimestamp(LocalDateTime.now())
                .build();
        Bid savedBid = bidRepository.save(newBid);

        // 10. Update Player Winning State
        player.setCurrentBidPrice(effectiveBidAmount);
        player.setCurrentWinningTeam(team);
        playerRepository.save(player);

        log.info("Bid successfully accepted: Player={}, Team={}, Amount={}",
                player.getFullName(), team.getShortCode(), BidResponse.formatCurrency(effectiveBidAmount));

        Long nextPossibleBid = calculateNextMinimumBid(effectiveBidAmount);

        return BidResponse.builder()
                .bidId(savedBid.getId())
                .auctionId(auction.getId())
                .playerId(player.getId())
                .playerName(player.getFullName())
                .teamId(team.getId())
                .teamName(team.getTeamName())
                .teamShortCode(team.getShortCode())
                .bidAmount(effectiveBidAmount)
                .formattedBidAmount(BidResponse.formatCurrency(effectiveBidAmount))
                .nextMinimumBid(nextPossibleBid)
                .formattedNextMinimumBid(BidResponse.formatCurrency(nextPossibleBid))
                .bidStatus(savedBid.getBidStatus().name())
                .bidTimestamp(savedBid.getBidTimestamp())
                .build();
    }

    /**
     * Standard IPL Incremental Bidding Slabs:
     * - Under ₹1 Crore: + ₹10 Lakhs (1,000,000)
     * - ₹1 Crore to ₹5 Crores: + ₹20 Lakhs (2,000,000)
     * - ₹5 Crores to ₹10 Crores: + ₹25 Lakhs (2,500,000)
     * - Above ₹10 Crores: + ₹50 Lakhs (5,000,000)
     */
    public Long calculateNextMinimumBid(Long currentBidAmount) {
        if (currentBidAmount == null || currentBidAmount <= 0) {
            return 2000000L; // Default base 20 Lakhs
        }
        if (currentBidAmount < 10000000L) {
            return currentBidAmount + 1000000L;  // + 10 Lakhs
        } else if (currentBidAmount < 50000000L) {
            return currentBidAmount + 2000000L;  // + 20 Lakhs
        } else if (currentBidAmount < 100000000L) {
            return currentBidAmount + 2500000L;  // + 25 Lakhs
        } else {
            return currentBidAmount + 5000000L;  // + 50 Lakhs
        }
    }

    @Transactional(readOnly = true)
    public CurrentBidView getCurrentBiddingState(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with ID: " + playerId));

        Long currentPrice = (player.getCurrentBidPrice() != null && player.getCurrentBidPrice() > 0)
                ? player.getCurrentBidPrice()
                : player.getBasePrice();

        Long nextMinBid = (player.getCurrentWinningTeam() == null && (player.getCurrentBidPrice() == null || player.getCurrentBidPrice() == 0))
                ? player.getBasePrice()
                : calculateNextMinimumBid(currentPrice);

        Bid latestBid = bidRepository.findTopByPlayerIdOrderByBidAmountDesc(playerId).orElse(null);

        return CurrentBidView.builder()
                .playerId(player.getId())
                .fullName(player.getFullName())
                .role(player.getRole())
                .country(player.getCountry())
                .overseas(Boolean.TRUE.equals(player.getIsOverseas()))
                .basePrice(player.getBasePrice())
                .formattedBasePrice(BidResponse.formatCurrency(player.getBasePrice()))
                .currentBidPrice(player.getCurrentBidPrice())
                .formattedCurrentBidPrice(BidResponse.formatCurrency(player.getCurrentBidPrice()))
                .nextMinimumBid(nextMinBid)
                .formattedNextMinimumBid(BidResponse.formatCurrency(nextMinBid))
                .status(player.getStatus())
                .winningTeamId(player.getCurrentWinningTeam() != null ? player.getCurrentWinningTeam().getId() : null)
                .winningTeamName(player.getCurrentWinningTeam() != null ? player.getCurrentWinningTeam().getTeamName() : null)
                .winningTeamCode(player.getCurrentWinningTeam() != null ? player.getCurrentWinningTeam().getShortCode() : null)
                .totalBidsPlaced(bidRepository.countByAuctionIdAndPlayerId(1L, playerId))
                .lastBidTime(latestBid != null ? latestBid.getBidTimestamp() : null)
                .build();
    }

    @Transactional(readOnly = true)
    public List<BidResponse> getPlayerBidHistory(Long playerId, int limit) {
        return bidRepository.findByPlayerIdOrderByBidTimestampDesc(playerId, PageRequest.of(0, limit))
                .stream()
                .map(bid -> BidResponse.builder()
                        .bidId(bid.getId())
                        .auctionId(bid.getAuction().getId())
                        .playerId(bid.getPlayer().getId())
                        .playerName(bid.getPlayer().getFullName())
                        .teamId(bid.getTeam().getId())
                        .teamName(bid.getTeam().getTeamName())
                        .teamShortCode(bid.getTeam().getShortCode())
                        .bidAmount(bid.getBidAmount())
                        .formattedBidAmount(BidResponse.formatCurrency(bid.getBidAmount()))
                        .bidStatus(bid.getBidStatus().name())
                        .bidTimestamp(bid.getBidTimestamp())
                        .build())
                .collect(Collectors.toList());
    }
}
