package com.ipl.auction.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI iplAuctionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("IPL Auction Engine & Live Bidding API")
                        .description("High-concurrency live bidding engine and database architecture for the IPL Mega Auction Management System.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Member 3 - Database & Bidding Engine Developer")
                                .email("developer@iplauction.com"))
                        .license(new License().name("Apache 2.0").url("https://spring.io")));
    }
}
