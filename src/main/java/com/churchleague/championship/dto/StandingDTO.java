package com.churchleague.championship.dto;

import com.churchleague.championship.model.Team;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StandingDTO {

    private Long teamId;
    private String teamName;

    private int played;
    private int wins;
    private int draws;
    private int losses;

    private int goalsFor;
    private int goalsAgainst;
    private int goalDifference;

    private int points;
    private int position;

    // ==== FACTORY ====
    public static StandingDTO fromTeam(Team team) {
        return StandingDTO.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .played(0)
                .wins(0)
                .draws(0)
                .losses(0)
                .goalsFor(0)
                .goalsAgainst(0)
                .goalDifference(0)
                .points(0)
                .position(0)
                .build();
    }

    // ==== HELPERS ====

    public void incPlayed() {
        this.played++;
    }

    public void incWins() {
        this.wins++;
    }

    public void incDraws() {
        this.draws++;
    }

    public void incLosses() {
        this.losses++;
    }

    public void addGoalsFor(int value) {
        this.goalsFor += value;
    }

    public void addGoalsAgainst(int value) {
        this.goalsAgainst += value;
    }

    public void addGoalDifference(int value) {
        this.goalDifference += value;
    }

    public void addPoints(int value) {
        this.points += value;
    }
}