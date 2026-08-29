package com.ipl.auction.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamRequest {

    @NotBlank(message = "Team name is required")
    @Size(max = 100, message = "Team name cannot exceed 100 characters")
    private String teamName;

    @NotBlank(message = "Short code is required")
    @Size(min = 2, max = 10, message = "Short code must be between 2 and 10 characters")
    private String shortCode;

    private String logoUrl;

    @NotNull(message = "Total purse is required")
    @Min(value = 10000000, message = "Total purse must be at least ₹1 Crore (10,000,000 INR)")
    private Long totalPurse;

    @Builder.Default
    private Integer maxSquadSize = 25;

    @Builder.Default
    private Integer minSquadSize = 18;

    @Builder.Default
    private Integer maxForeignPlayers = 8;
}
