package com.ipl.auction.dto.request;

import com.ipl.auction.entity.enums.PlayerRole;
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
public class PlayerRequest {

    @NotBlank(message = "Player full name is required")
    @Size(max = 120, message = "Name cannot exceed 120 characters")
    private String fullName;

    @NotNull(message = "Player role is required (BATSMAN, BOWLER, ALL_ROUNDER, WICKET_KEEPER)")
    private PlayerRole role;

    @NotBlank(message = "Country is required")
    private String country;

    @Builder.Default
    private Boolean isOverseas = false;

    private Integer age;

    @NotNull(message = "Base price is required")
    @Min(value = 1000000, message = "Base price must be at least ₹10 Lakhs (1,000,000 INR)")
    private Long basePrice;

    private String auctionSetCategory;

    private String photoUrl;
}
