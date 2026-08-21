package com.ipl.auction.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerRequestDto {

    @NotBlank(message = "Player name is required")
    private String name;

    @NotBlank(message = "Category is required (BATSMAN, BOWLER, ALL_ROUNDER, WICKET_KEEPER)")
    private String category;

    @NotBlank(message = "Nationality is required (INDIAN, OVERSEAS)")
    private String nationality;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "200000.0", message = "Base price must be at least 200,000")
    private BigDecimal basePrice;
}
