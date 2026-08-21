package com.ipl.auction.dto.request;

import jakarta.validation.constraints.DecimalMin;
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
public class PlayerPurchaseRequestDto {

    @NotNull(message = "Team ID is required")
    private Long teamId;

    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "200000.0", message = "Purchase price must be at least 200,000")
    private BigDecimal purchasePrice;
}
