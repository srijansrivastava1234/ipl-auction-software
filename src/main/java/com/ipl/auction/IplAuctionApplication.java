package com.ipl.auction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class IplAuctionApplication {

    public static void main(String[] args) {
        SpringApplication.run(IplAuctionApplication.class, args);
    }
}
