package com.churchleague.championship.model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "goal_events")
@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GoalEvent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false)
    private Match match;

    @ManyToOne(optional = false)
    private Player player;

    @Column(name = "goal_minute")
    private Integer minute;
}
