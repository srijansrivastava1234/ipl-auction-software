package com.ipl.auction.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3 / Swagger UI Configuration for IPL Auction Software.
 * Maintained by Member 5 (QA, Testing & API Documentation Lead).
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI iplAuctionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("IPL Auction Software API Documentation")
                        .description("Comprehensive REST API documentation and OpenAPI spec for IPL Auction Software system, " +
                                "including Team Management, Player Catalog, Live Bidding Engine, and Security endpoints.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("IPL Auction Development Team")
                                .email("qa-team@iplauction.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Provide JWT Bearer Token to access secured IPL Auction APIs.")));
    }
}
