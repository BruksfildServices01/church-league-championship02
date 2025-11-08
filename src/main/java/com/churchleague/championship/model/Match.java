package com.churchleague.championship.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;   // <-- IMPORTA

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private Integer roundNumber; // rodada: 1, 2, 3...

    @ManyToOne(optional = false)
    private Tournament tournament;

    @ManyToOne(optional = false)
    private Team homeTeam;

    @ManyToOne(optional = false)
    private Team awayTeam;

    private Integer homeGoals;
    private Integer awayGoals;

    // NOVOS CAMPOS 👇
    private LocalDate matchDate;
    private String location;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MatchStatus status = MatchStatus.AGENDADO;

    @Column(length = 500)
    private String notes;
}
