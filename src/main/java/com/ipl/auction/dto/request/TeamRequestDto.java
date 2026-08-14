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
public class TeamRequestDto {

    @NotBlank(message = "Team name is required")
    private String name;

    @NotBlank(message = "Short name is required (e.g. CSK, MI)")
    private String shortName;

    @NotNull(message = "Total purse is required")
    @DecimalMin(value = "100000.0", message = "Total purse must be at least 100,000")
    private BigDecimal totalPurse;

    private Integer maxSquadSize;
}
