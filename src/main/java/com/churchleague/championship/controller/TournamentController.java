package com.churchleague.championship.controller;

import com.churchleague.championship.dto.ScorerDTO;
import com.churchleague.championship.dto.StandingDTO;
import com.churchleague.championship.dto.TournamentDashboardDTO;
import com.churchleague.championship.model.Team;
import com.churchleague.championship.model.Tournament;
import com.churchleague.championship.service.MatchService;
import com.churchleague.championship.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService service;
    private final MatchService matchService;
    @PostMapping
    public Tournament create(@RequestBody Tournament t) {
        return service.create(t);
    }

    @GetMapping
    public List<Tournament> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public Tournament get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Tournament update(@PathVariable Long id, @RequestBody Tournament t) {
        return service.update(id, t);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // ===== INSCRIÇÃO =====

    @PostMapping("/{tournamentId}/teams/{teamId}")
    public Tournament addTeam(@PathVariable Long tournamentId, @PathVariable Long teamId) {
        return service.addTeamToTournament(tournamentId, teamId);
    }

    @DeleteMapping("/{tournamentId}/teams/{teamId}")
    public Tournament removeTeam(@PathVariable Long tournamentId, @PathVariable Long teamId) {
        return service.removeTeamFromTournament(tournamentId, teamId);
    }

    @GetMapping("/{tournamentId}/teams")
    public List<Team> listTeams(@PathVariable Long tournamentId) {
        return service.listTeamsFromTournament(tournamentId);
    }

    // ===== CLASSIFICAÇÃO =====
    @GetMapping("/{tournamentId}/standings")
    public List<StandingDTO> standings(@PathVariable Long tournamentId) {
        return service.getStandings(tournamentId);
    }

    @GetMapping("/{tournamentId}/dashboard")
    public TournamentDashboardDTO dashboard(@PathVariable Long tournamentId) {
        return service.buildDashboard(tournamentId);
    }

    @GetMapping("/{tournamentId}/scorers")
    public List<ScorerDTO> tournamentScorers(@PathVariable Long tournamentId) {
        return matchService.tournamentScorers(tournamentId);
    }

}