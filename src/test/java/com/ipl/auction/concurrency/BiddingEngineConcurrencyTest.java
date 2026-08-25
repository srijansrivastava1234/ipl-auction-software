package com.ipl.auction.concurrency;

import com.ipl.auction.dto.request.BidRequest;
import com.ipl.auction.entity.Auction;
import com.ipl.auction.entity.Player;
import com.ipl.auction.entity.Team;
import com.ipl.auction.entity.enums.AuctionStatus;
import com.ipl.auction.entity.enums.PlayerRole;
import com.ipl.auction.entity.enums.PlayerStatus;
import com.ipl.auction.repository.AuctionRepository;
import com.ipl.auction.repository.BidRepository;
import com.ipl.auction.repository.PlayerRepository;
import com.ipl.auction.repository.TeamRepository;
import com.ipl.auction.service.BiddingEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class BiddingEngineConcurrencyTest {

    @Autowired
    private BiddingEngineService biddingEngineService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    private Auction auction;
    private Player player;
    private List<Team> teams;

    @BeforeEach
    void setup() {
        auction = auctionRepository.findAll().stream().findFirst()
                .orElseGet(() -> auctionRepository.save(Auction.builder()
                        .title("Concurrency Test Mega Auction")
                        .year(2025)
                        .status(AuctionStatus.LIVE)
                        .build()));

        player = playerRepository.save(Player.builder()
                .fullName("Jasprit Bumrah")
                .role(PlayerRole.BOWLER)
                .country("India")
                .isOverseas(false)
                .basePrice(20000000L) // 2.00 Crore
                .currentBidPrice(0L)
                .status(PlayerStatus.IN_AUCTION)
                .build());

        teams = teamRepository.findAll();
        if (teams.size() < 4) {
            teams = new ArrayList<>();
            for (int i = 1; i <= 6; i++) {
                teams.add(teamRepository.save(Team.builder()
                        .teamName("Concurrent Franchise " + i)
                        .shortCode("CF" + i)
                        .totalPurse(1000000000L)
                        .remainingPurse(1000000000L)
                        .maxSquadSize(25)
                        .minSquadSize(18)
                        .maxForeignPlayers(8)
                        .build()));
            }
        }
    }

    @Test
    @DisplayName("Simultaneous concurrent bids should execute safely with Pessimistic Locking preventing race conditions")
    void testConcurrentBiddingPessimisticLock() throws InterruptedException {
        int numberOfThreads = Math.min(6, teams.size());
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(numberOfThreads);

        AtomicInteger successfulBids = new AtomicInteger(0);
        AtomicInteger rejectedBids = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            final Team biddingTeam = teams.get(i);
            executorService.submit(() -> {
                try {
                    startGate.await(); // Wait for all threads to be ready for simultaneous trigger
                    
                    // Each thread attempts to place a dynamically calculated incremental bid
                    biddingEngineService.placeBid(BidRequest.builder()
                            .auctionId(auction.getId())
                            .playerId(player.getId())
                            .teamId(biddingTeam.getId())
                            .build());
                    
                    successfulBids.incrementAndGet();
                } catch (Exception e) {
                    rejectedBids.incrementAndGet();
                } finally {
                    endGate.countDown();
                }
            });
        }

        // Fire all threads simultaneously
        startGate.countDown();
        endGate.await();
        executorService.shutdown();

        // Verification
        Player finalPlayer = playerRepository.findById(player.getId()).orElseThrow();
        long totalRecordedBids = bidRepository.countByAuctionIdAndPlayerId(auction.getId(), player.getId());

        System.out.println("Concurrent Test Execution Results:");
        System.out.println("Total Threads: " + numberOfThreads);
        System.out.println("Successful Bids: " + successfulBids.get());
        System.out.println("Rejected Bids (Lock/Increment contention): " + rejectedBids.get());
        System.out.println("Final Player Price: " + finalPlayer.getCurrentBidPrice());
        Team winningTeam = null;
        if (finalPlayer.getCurrentWinningTeam() != null) {
            winningTeam = teamRepository.findById(finalPlayer.getCurrentWinningTeam().getId()).orElse(null);
        }
        System.out.println("Final Winning Team: " + (winningTeam != null ? winningTeam.getShortCode() : "None"));

        assertTrue(successfulBids.get() >= 1, "At least one bid must succeed");
        assertEquals(successfulBids.get(), totalRecordedBids, "Total recorded bids in DB must exactly match successful bids");
        assertTrue(finalPlayer.getCurrentBidPrice() >= 20000000L, "Final price must be at least base price");
    }
}
