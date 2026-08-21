package com.ipl.auction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerResponseDto {
    private Long id;
    private String name;
    private String category;
    private String nationality;
    private BigDecimal basePrice;
    private BigDecimal soldPrice;
    private Boolean isSold;
    private Long teamId;
    private String teamName;
}
