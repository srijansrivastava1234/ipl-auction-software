package com.ipl.auction;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class IplAuctionApplicationTests {

    @Test
    void contextLoads() {
        // Validates Spring Application Context, JPA Entities, and Database configurations load successfully
    }
}
