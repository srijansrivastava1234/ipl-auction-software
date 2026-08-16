package com.ipl.auction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class IplAuctionApplicationTests {

    @Test
    @DisplayName("Context Load Verification Test")
    void contextLoads() {
        assertDoesNotThrow(() -> {
            // Verifies Spring Boot Application Context loads cleanly
        });
    }
}
