package com.churchleague.championship.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentType type; // PONTOS_CORRIDOS ou PONTOS_CORRIDOS_FINAL

    private boolean hasReturn;   // ida e volta?

    // Regras básicas (valores padrão)
    @Builder.Default private int pointsWin  = 3;
    @Builder.Default private int pointsDraw = 1;
    @Builder.Default private int pointsLoss = 0;

    // Placar padrão de W.O.
    @Builder.Default private int woHomeGoals = 3;
    @Builder.Default private int woAwayGoals = 0;

    private String defaultVenue; // opcional

    /** NOVO: times inscritos nesse campeonato */
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "tournament_teams",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private Set<Team> teams = new HashSet<>();



}