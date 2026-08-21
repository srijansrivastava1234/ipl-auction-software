package com.ipl.auction.builder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Test Data Builder for creating mock objects during JUnit 5 & Mockito test runs.
 * Maintained by Member 5 (QA & Testing Lead).
 */
public class TestDataBuilder {

    public static Map<String, Object> createMockTeam(Long id, String teamName, BigDecimal remainingPurse) {
        Map<String, Object> team = new HashMap<>();
        team.put("id", id);
        team.put("teamName", teamName);
        team.put("remainingPurse", remainingPurse);
        team.put("maxPurseLimit", new BigDecimal("100000000.00")); // 100 Cr INR limit
        team.put("totalPlayersCount", 0);
        return team;
    }

    public static Map<String, Object> createMockPlayer(Long id, String name, String category, BigDecimal basePrice) {
        Map<String, Object> player = new HashMap<>();
        player.put("id", id);
        player.put("name", name);
        player.put("category", category);
        player.put("basePrice", basePrice);
        player.put("status", "UNSOLD");
        return player;
    }

    public static Map<String, Object> createMockBidRequest(Long teamId, Long playerId, BigDecimal bidAmount) {
        Map<String, Object> bid = new HashMap<>();
        bid.put("teamId", teamId);
        bid.put("playerId", playerId);
        bid.put("bidAmount", bidAmount);
        return bid;
    }
}
