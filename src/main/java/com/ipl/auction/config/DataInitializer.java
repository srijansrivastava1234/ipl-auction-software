package com.ipl.auction.config;

import com.ipl.auction.entity.Auction;
import com.ipl.auction.entity.Player;
import com.ipl.auction.entity.Team;
import com.ipl.auction.entity.enums.AuctionStatus;
import com.ipl.auction.entity.enums.PlayerRole;
import com.ipl.auction.entity.enums.PlayerStatus;
import com.ipl.auction.repository.AuctionRepository;
import com.ipl.auction.repository.PlayerRepository;
import com.ipl.auction.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final AuctionRepository auctionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (teamRepository.count() == 0) {
            log.info("Seeding Initial IPL Franchises...");
            seedTeams();
        }

        if (playerRepository.count() == 0) {
            log.info("Seeding Initial IPL Players...");
            seedPlayers();
        }

        if (auctionRepository.count() == 0) {
            log.info("Seeding Initial IPL Auction Event...");
            seedAuction();
        }
    }

    private void seedTeams() {
        List<Team> teams = Arrays.asList(
                Team.builder().teamName("Chennai Super Kings").shortCode("CSK").logoUrl("https://assets.ipl.com/csk.png").totalPurse(1000000000L).remainingPurse(1000000000L).maxSquadSize(25).minSquadSize(18).maxForeignPlayers(8).currentSquadCount(0).currentForeignCount(0).build(),
                Team.builder().teamName("Mumbai Indians").shortCode("MI").logoUrl("https://assets.ipl.com/mi.png").totalPurse(1000000000L).remainingPurse(1000000000L).maxSquadSize(25).minSquadSize(18).maxForeignPlayers(8).currentSquadCount(0).currentForeignCount(0).build(),
                Team.builder().teamName("Royal Challengers Bengaluru").shortCode("RCB").logoUrl("https://assets.ipl.com/rcb.png").totalPurse(1000000000L).remainingPurse(1000000000L).maxSquadSize(25).minSquadSize(18).maxForeignPlayers(8).currentSquadCount(0).currentForeignCount(0).build(),
                Team.builder().teamName("Kolkata Knight Riders").shortCode("KKR").logoUrl("https://assets.ipl.com/kkr.png").totalPurse(1000000000L).remainingPurse(1000000000L).maxSquadSize(25).minSquadSize(18).maxForeignPlayers(8).currentSquadCount(0).currentForeignCount(0).build(),
                Team.builder().teamName("Rajasthan Royals").shortCode("RR").logoUrl("https://assets.ipl.com/rr.png").totalPurse(1000000000L).remainingPurse(1000000000L).maxSquadSize(25).minSquadSize(18).maxForeignPlayers(8).currentSquadCount(0).currentForeignCount(0).build(),
                Team.builder().teamName("Sunrisers Hyderabad").shortCode("SRH").logoUrl("https://assets.ipl.com/srh.png").totalPurse(1000000000L).remainingPurse(1000000000L).maxSquadSize(25).minSquadSize(18).maxForeignPlayers(8).currentSquadCount(0).currentForeignCount(0).build(),
                Team.builder().teamName("Delhi Capitals").shortCode("DC").logoUrl("https://assets.ipl.com/dc.png").totalPurse(1000000000L).remainingPurse(1000000000L).maxSquadSize(25).minSquadSize(18).maxForeignPlayers(8).currentSquadCount(0).currentForeignCount(0).build(),
                Team.builder().teamName("Lucknow Super Giants").shortCode("LSG").logoUrl("https://assets.ipl.com/lsg.png").totalPurse(1000000000L).remainingPurse(1000000000L).maxSquadSize(25).minSquadSize(18).maxForeignPlayers(8).currentSquadCount(0).currentForeignCount(0).build(),
                Team.builder().teamName("Gujarat Titans").shortCode("GT").logoUrl("https://assets.ipl.com/gt.png").totalPurse(1000000000L).remainingPurse(1000000000L).maxSquadSize(25).minSquadSize(18).maxForeignPlayers(8).currentSquadCount(0).currentForeignCount(0).build(),
                Team.builder().teamName("Punjab Kings").shortCode("PBKS").logoUrl("https://assets.ipl.com/pbks.png").totalPurse(1000000000L).remainingPurse(1000000000L).maxSquadSize(25).minSquadSize(18).maxForeignPlayers(8).currentSquadCount(0).currentForeignCount(0).build()
        );
        teamRepository.saveAll(teams);
        log.info("Successfully seeded {} teams", teams.size());
    }

    private void seedPlayers() {
        List<Player> players = Arrays.asList(
                Player.builder().fullName("Rishabh Pant").role(PlayerRole.WICKET_KEEPER).country("India").isOverseas(false).age(27).basePrice(20000000L).currentBidPrice(0L).status(PlayerStatus.AVAILABLE).auctionSetCategory("MARQUEE_SET_1").photoUrl("https://assets.ipl.com/pant.png").build(),
                Player.builder().fullName("Shreyas Iyer").role(PlayerRole.BATSMAN).country("India").isOverseas(false).age(29).basePrice(20000000L).currentBidPrice(0L).status(PlayerStatus.AVAILABLE).auctionSetCategory("MARQUEE_SET_1").photoUrl("https://assets.ipl.com/iyer.png").build(),
                Player.builder().fullName("Mitchell Starc").role(PlayerRole.BOWLER).country("Australia").isOverseas(true).age(34).basePrice(20000000L).currentBidPrice(0L).status(PlayerStatus.AVAILABLE).auctionSetCategory("MARQUEE_SET_1").photoUrl("https://assets.ipl.com/starc.png").build(),
                Player.builder().fullName("Jos Buttler").role(PlayerRole.WICKET_KEEPER).country("England").isOverseas(true).age(34).basePrice(20000000L).currentBidPrice(0L).status(PlayerStatus.AVAILABLE).auctionSetCategory("MARQUEE_SET_1").photoUrl("https://assets.ipl.com/buttler.png").build(),
                Player.builder().fullName("KL Rahul").role(PlayerRole.BATSMAN).country("India").isOverseas(false).age(32).basePrice(20000000L).currentBidPrice(0L).status(PlayerStatus.AVAILABLE).auctionSetCategory("MARQUEE_SET_1").photoUrl("https://assets.ipl.com/rahul.png").build(),
                Player.builder().fullName("Arshdeep Singh").role(PlayerRole.BOWLER).country("India").isOverseas(false).age(25).basePrice(20000000L).currentBidPrice(0L).status(PlayerStatus.AVAILABLE).auctionSetCategory("MARQUEE_SET_1").photoUrl("https://assets.ipl.com/arshdeep.png").build(),
                Player.builder().fullName("David Miller").role(PlayerRole.BATSMAN).country("South Africa").isOverseas(true).age(35).basePrice(15000000L).currentBidPrice(0L).status(PlayerStatus.AVAILABLE).auctionSetCategory("BATSMAN_SET_1").photoUrl("https://assets.ipl.com/miller.png").build(),
                Player.builder().fullName("Yuzvendra Chahal").role(PlayerRole.BOWLER).country("India").isOverseas(false).age(34).basePrice(20000000L).currentBidPrice(0L).status(PlayerStatus.AVAILABLE).auctionSetCategory("SPINNER_SET_1").photoUrl("https://assets.ipl.com/chahal.png").build(),
                Player.builder().fullName("Liam Livingstone").role(PlayerRole.ALL_ROUNDER).country("England").isOverseas(true).age(31).basePrice(20000000L).currentBidPrice(0L).status(PlayerStatus.AVAILABLE).auctionSetCategory("ALLROUNDER_SET_1").photoUrl("https://assets.ipl.com/livingstone.png").build(),
                Player.builder().fullName("Mohammed Shami").role(PlayerRole.BOWLER).country("India").isOverseas(false).age(34).basePrice(20000000L).currentBidPrice(0L).status(PlayerStatus.AVAILABLE).auctionSetCategory("PACER_SET_1").photoUrl("https://assets.ipl.com/shami.png").build(),
                Player.builder().fullName("Glenn Maxwell").role(PlayerRole.ALL_ROUNDER).country("Australia").isOverseas(true).age(36).basePrice(20000000L).currentBidPrice(0L).status(PlayerStatus.AVAILABLE).auctionSetCategory("ALLROUNDER_SET_1").photoUrl("https://assets.ipl.com/maxwell.png").build(),
                Player.builder().fullName("Washington Sundar").role(PlayerRole.ALL_ROUNDER).country("India").isOverseas(false).age(25).basePrice(10000000L).currentBidPrice(0L).status(PlayerStatus.AVAILABLE).auctionSetCategory("ALLROUNDER_SET_1").photoUrl("https://assets.ipl.com/sundar.png").build()
        );
        playerRepository.saveAll(players);
        log.info("Successfully seeded {} players", players.size());
    }

    private void seedAuction() {
        Auction auction = Auction.builder()
                .title("IPL 2025 Mega Auction")
                .year(2025)
                .status(AuctionStatus.LIVE)
                .startTime(LocalDateTime.now())
                .build();
        auctionRepository.save(auction);
        log.info("Successfully seeded active auction: {}", auction.getTitle());
    }
}
