package com.ipl.auction.config;

import com.ipl.auction.model.Player;
import com.ipl.auction.model.Player.PlayerStatus;
import com.ipl.auction.model.Team;
import com.ipl.auction.repository.PlayerRepository;
import com.ipl.auction.repository.TeamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public DataInitializer(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Clear any existing records first
        playerRepository.deleteAll();
        teamRepository.deleteAll();

        // Seed All 10 Official IPL Teams
        List<Team> iplTeams = Arrays.asList(
            new Team("Chennai Super Kings", new BigDecimal("1000000000")),
            new Team("Mumbai Indians", new BigDecimal("1000000000")),
            new Team("Royal Challengers Bengaluru", new BigDecimal("1000000000")),
            new Team("Kolkata Knight Riders", new BigDecimal("1000000000")),
            new Team("Rajasthan Royals", new BigDecimal("1000000000")),
            new Team("Sunrisers Hyderabad", new BigDecimal("1000000000")),
            new Team("Delhi Capitals", new BigDecimal("1000000000")),
            new Team("Gujarat Titans", new BigDecimal("1000000000")),
            new Team("Lucknow Super Giants", new BigDecimal("1000000000")),
            new Team("Punjab Kings", new BigDecimal("1000000000"))
        );
        teamRepository.saveAll(iplTeams);
        System.out.println("=================================================");
        System.out.println("✅ SUCCESSFULLY SEEDED 10 IPL TEAMS!");

        // Seed Expanded 23-Player Pool
        List<Player> playerPool = Arrays.asList(
            // Marquee Batsmen
            createPlayer("Virat Kohli", "Batsman", "20000000"),
            createPlayer("Rohit Sharma", "Batsman", "20000000"),
            createPlayer("Suryakumar Yadav", "Batsman", "20000000"),
            createPlayer("Shubman Gill", "Batsman", "20000000"),
            createPlayer("Yashasvi Jaiswal", "Batsman", "20000000"),
            createPlayer("Travis Head", "Batsman", "20000000"),
            
            // Wicketkeeper Batsmen
            createPlayer("MS Dhoni", "Wicketkeeper-Batsman", "20000000"),
            createPlayer("Rishabh Pant", "Wicketkeeper-Batsman", "20000000"),
            createPlayer("KL Rahul", "Wicketkeeper-Batsman", "20000000"),
            createPlayer("Heinrich Klaasen", "Wicketkeeper-Batsman", "20000000"),
            createPlayer("Sanju Samson", "Wicketkeeper-Batsman", "20000000"),

            // All-Rounders
            createPlayer("Hardik Pandya", "All-Rounder", "15000000"),
            createPlayer("Ravindra Jadeja", "All-Rounder", "20000000"),
            createPlayer("Axar Patel", "All-Rounder", "15000000"),
            createPlayer("Sunil Narine", "All-Rounder", "15000000"),
            createPlayer("Marcus Stoinis", "All-Rounder", "15000000"),

            // Bowlers
            createPlayer("Jasprit Bumrah", "Bowler", "20000000"),
            createPlayer("Kuldeep Yadav", "Bowler", "15000000"),
            createPlayer("Rashid Khan", "Bowler", "20000000"),
            createPlayer("Mitchell Starc", "Bowler", "20000000"),
            createPlayer("Pat Cummins", "Bowler", "20000000"),
            createPlayer("Yuzvendra Chahal", "Bowler", "15000000"),
            createPlayer("Mohammed Siraj", "Bowler", "15000000")
        );

        playerRepository.saveAll(playerPool);
        System.out.println("✅ SUCCESSFULLY SEEDED 23 IPL PLAYERS!");
        System.out.println("=================================================");
    }

    private Player createPlayer(String name, String role, String basePriceStr) {
        Player player = new Player();
        player.setName(name);
        player.setRole(role);
        player.setBasePrice(new BigDecimal(basePriceStr));
        player.setStatus(PlayerStatus.UNSOLD);
        return player;
    }
}