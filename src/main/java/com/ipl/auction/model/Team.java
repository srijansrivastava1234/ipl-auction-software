package com.ipl.auction.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 10)
    private String shortName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPurse;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal remainingPurse;

    @Column(nullable = false)
    @Builder.Default
    private Integer maxSquadSize = 25;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Player> players = new ArrayList<>();
}
