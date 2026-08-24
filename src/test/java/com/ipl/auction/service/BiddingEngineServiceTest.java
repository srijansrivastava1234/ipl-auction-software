package com.ipl.auction.service;

import com.ipl.auction.dto.request.BidRequest;
import com.ipl.auction.dto.response.BidResponse;
import com.ipl.auction.entity.Auction;
import com.ipl.auction.entity.Player;
import com.ipl.auction.entity.Team;
import com.ipl.auction.entity.enums.AuctionStatus;
import com.ipl.auction.entity.enums.PlayerRole;
import com.ipl.auction.entity.enums.PlayerStatus;
import com.ipl.auction.exception.InsufficientPurseException;
import com.ipl.auction.exception.InvalidBidException;
import com.ipl.auction.exception.PlayerNotAvailableException;
import com.ipl.auction.exception.SquadLimitExceededException;
import com.ipl.auction.repository.AuctionRepository;
import com.ipl.auction.repository.BidRepository;
import com.ipl.auction.repository.PlayerRepository;
import com.ipl.auction.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BiddingEngineServiceTest {

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

    private Auction testAuction;
    private Player testPlayer;
    private Team teamCsk;
    private Team teamMi;

    @BeforeEach
    void setUp() {
        testAuction = auctionRepository.findAll().stream().findFirst()
                .orElseGet(() -> auctionRepository.save(Auction.builder()
                        .title("Test IPL Auction")
                        .year(2025)
                        .status(AuctionStatus.LIVE)
                        .build()));

        teamCsk = teamRepository.findByShortCode("CSK")
                .orElseGet(() -> teamRepository.save(Team.builder()
                        .teamName("Chennai Super Kings")
                        .shortCode("CSK")
                        .totalPurse(1000000000L)
                        .remainingPurse(1000000000L)
                        .maxSquadSize(25)
                        .minSquadSize(18)
                        .maxForeignPlayers(8)
                        .build()));

        teamMi = teamRepository.findByShortCode("MI")
                .orElseGet(() -> teamRepository.save(Team.builder()
                        .teamName("Mumbai Indians")
                        .shortCode("MI")
                        .totalPurse(1000000000L)
                        .remainingPurse(1000000000L)
                        .maxSquadSize(25)
                        .minSquadSize(18)
                        .maxForeignPlayers(8)
                        .build()));

        testPlayer = playerRepository.save(Player.builder()
                .fullName("Test Cricketer")
                .role(PlayerRole.BATSMAN)
                .country("India")
                .isOverseas(false)
                .basePrice(20000000L) // ₹2.00 Crore
                .currentBidPrice(0L)
                .status(PlayerStatus.IN_AUCTION)
                .build());
    }

    @Test
    @DisplayName("Opening bid at Base Price should be accepted successfully")
    void testPlaceOpeningBid() {
        BidRequest request = BidRequest.builder()
                .auctionId(testAuction.getId())
                .playerId(testPlayer.getId())
                .teamId(teamCsk.getId())
                .bidAmount(20000000L)
                .build();

        BidResponse response = biddingEngineService.placeBid(request);

        assertNotNull(response);
        assertEquals(20000000L, response.getBidAmount());
        assertEquals("CSK", response.getTeamShortCode());
        assertEquals("ACCEPTED", response.getBidStatus());
        assertEquals(22000000L, response.getNextMinimumBid()); // +20L slab for 2Cr
    }

    @Test
    @DisplayName("Subsequent valid bid from competitor team should outbid previous leader")
    void testOutbidCompetitor() {
        // First bid by CSK at ₹2.00 Cr
        biddingEngineService.placeBid(BidRequest.builder()
                .auctionId(testAuction.getId())
                .playerId(testPlayer.getId())
                .teamId(teamCsk.getId())
                .bidAmount(20000000L)
                .build());

        // Second bid by MI at ₹2.20 Cr
        BidResponse responseMi = biddingEngineService.placeBid(BidRequest.builder()
                .auctionId(testAuction.getId())
                .playerId(testPlayer.getId())
                .teamId(teamMi.getId())
                .bidAmount(22000000L)
                .build());

        assertEquals(22000000L, responseMi.getBidAmount());
        assertEquals("MI", responseMi.getTeamShortCode());

        // Check updated player entity
        Player updatedPlayer = playerRepository.findById(testPlayer.getId()).orElseThrow();
        assertEquals(22000000L, updatedPlayer.getCurrentBidPrice());
        assertEquals(teamMi.getId(), updatedPlayer.getCurrentWinningTeam().getId());
    }

    @Test
    @DisplayName("Franchise cannot outbid itself when it already holds the highest bid")
    void testSelfOutbidShouldThrowException() {
        // CSK places opening bid
        biddingEngineService.placeBid(BidRequest.builder()
                .auctionId(testAuction.getId())
                .playerId(testPlayer.getId())
                .teamId(teamCsk.getId())
                .bidAmount(20000000L)
                .build());

        // CSK tries to bid again immediately
        BidRequest selfBid = BidRequest.builder()
                .auctionId(testAuction.getId())
                .playerId(testPlayer.getId())
                .teamId(teamCsk.getId())
                .bidAmount(22000000L)
                .build();

        assertThrows(InvalidBidException.class, () -> biddingEngineService.placeBid(selfBid));
    }

    @Test
    @DisplayName("Bid below minimum incremental slab should be rejected")
    void testBidBelowIncrementShouldThrowException() {
        // Opening bid at 2.00 Cr
        biddingEngineService.placeBid(BidRequest.builder()
                .auctionId(testAuction.getId())
                .playerId(testPlayer.getId())
                .teamId(teamCsk.getId())
                .bidAmount(20000000L)
                .build());

        // MI tries to bid 2.05 Cr (less than required 2.20 Cr)
        BidRequest invalidBid = BidRequest.builder()
                .auctionId(testAuction.getId())
                .playerId(testPlayer.getId())
                .teamId(teamMi.getId())
                .bidAmount(20500000L)
                .build();

        assertThrows(InvalidBidException.class, () -> biddingEngineService.placeBid(invalidBid));
    }

    @Test
    @DisplayName("Bidding on a player not currently IN_AUCTION should throw PlayerNotAvailableException")
    void testBiddingOnUnavailablePlayerShouldThrowException() {
        testPlayer.setStatus(PlayerStatus.AVAILABLE);
        playerRepository.save(testPlayer);

        BidRequest request = BidRequest.builder()
                .auctionId(testAuction.getId())
                .playerId(testPlayer.getId())
                .teamId(teamCsk.getId())
                .bidAmount(20000000L)
                .build();

        assertThrows(PlayerNotAvailableException.class, () -> biddingEngineService.placeBid(request));
    }

    @Test
    @DisplayName("Bidding exceeding team purse should throw InsufficientPurseException")
    void testExceedingPurseShouldThrowException() {
        teamCsk.setRemainingPurse(10000000L); // Only ₹1 Cr left
        teamRepository.save(teamCsk);

        BidRequest request = BidRequest.builder()
                .auctionId(testAuction.getId())
                .playerId(testPlayer.getId())
                .teamId(teamCsk.getId())
                .bidAmount(20000000L) // ₹2 Cr bid
                .build();

        assertThrows(InsufficientPurseException.class, () -> biddingEngineService.placeBid(request));
    }

    @Test
    @DisplayName("Overseas player bidding when team foreign quota is full should throw SquadLimitExceededException")
    void testOverseasQuotaExceededShouldThrowException() {
        Player overseasPlayer = playerRepository.save(Player.builder()
                .fullName("Overseas Star")
                .role(PlayerRole.ALL_ROUNDER)
                .country("Australia")
                .isOverseas(true)
                .basePrice(20000000L)
                .status(PlayerStatus.IN_AUCTION)
                .build());

        teamCsk.setCurrentForeignCount(8); // Max 8 foreign players reached
        teamRepository.save(teamCsk);

        BidRequest request = BidRequest.builder()
                .auctionId(testAuction.getId())
                .playerId(overseasPlayer.getId())
                .teamId(teamCsk.getId())
                .bidAmount(20000000L)
                .build();

        assertThrows(SquadLimitExceededException.class, () -> biddingEngineService.placeBid(request));
    }
}
