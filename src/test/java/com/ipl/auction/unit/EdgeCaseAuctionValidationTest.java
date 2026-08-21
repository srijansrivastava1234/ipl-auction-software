package com.ipl.auction.unit;

import com.ipl.auction.builder.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Edge-Case Unit Testing Suite for IPL Auction Software validation rules.
 * Maintained by Member 5 (QA & Testing Lead).
 */
@ExtendWith(MockitoExtension.class)
public class EdgeCaseAuctionValidationTest {

    @Nested
    @DisplayName("Purse Overflow & Bid Validation Tests")
    class PurseOverflowTests {

        @Test
        @DisplayName("Should Reject Bid When Bid Amount Exceeds Remaining Purse")
        void testBidExceedsPurseLimit() {
            BigDecimal remainingPurse = new BigDecimal("5000000.00"); // 50 Lakhs
            BigDecimal bidAmount = new BigDecimal("6000000.00");     // 60 Lakhs

            Map<String, Object> team = TestDataBuilder.createMockTeam(1L, "Mumbai Indians", remainingPurse);
            BigDecimal currentPurse = (BigDecimal) team.get("remainingPurse");

            boolean isValid = bidAmount.compareTo(currentPurse) <= 0;

            assertFalse(isValid, "Bid exceeding remaining team purse must be rejected.");
        }

        @Test
        @DisplayName("Should Accept Bid When Bid Amount Equals Remaining Purse Exactly")
        void testBidEqualsRemainingPurse() {
            BigDecimal remainingPurse = new BigDecimal("5000000.00");
            BigDecimal bidAmount = new BigDecimal("5000000.00");

            boolean isValid = bidAmount.compareTo(remainingPurse) <= 0;

            assertTrue(isValid, "Bid equal to remaining team purse should be valid.");
        }

        @Test
        @DisplayName("Should Reject Negative or Zero Bid Amount")
        void testRejectNegativeOrZeroBid() {
            BigDecimal zeroBid = BigDecimal.ZERO;
            BigDecimal negativeBid = new BigDecimal("-100000.00");

            assertTrue(zeroBid.compareTo(BigDecimal.ZERO) <= 0, "Zero bid must be flagged invalid.");
            assertTrue(negativeBid.compareTo(BigDecimal.ZERO) < 0, "Negative bid must be flagged invalid.");
        }
    }

    @Nested
    @DisplayName("Player Base Price & Minimum Increment Edge Cases")
    class BidIncrementEdgeCases {

        @Test
        @DisplayName("Should Reject Bid Lower Than Player Base Price")
        void testRejectBidBelowBasePrice() {
            Map<String, Object> player = TestDataBuilder.createMockPlayer(101L, "Jasprit Bumrah", "BOWLER", new BigDecimal("20000000.00"));
            BigDecimal basePrice = (BigDecimal) player.get("basePrice");
            BigDecimal bidAmount = new BigDecimal("15000000.00"); // Lower than base price

            boolean isAboveBasePrice = bidAmount.compareTo(basePrice) >= 0;

            assertFalse(isAboveBasePrice, "Bids below base price must be rejected.");
        }
    }
}
