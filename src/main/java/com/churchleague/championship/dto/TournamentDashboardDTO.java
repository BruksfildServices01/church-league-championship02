package com.churchleague.championship.dto;

import com.churchleague.championship.model.Match;
import com.churchleague.championship.model.Team;
import com.churchleague.championship.model.Tournament;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TournamentDashboardDTO {

    private Tournament tournament;
    private List<Team> teams;
    private List<StandingDTO> standings;
    private List<Match> nextMatches;
}
