package com.churchleague.championship.service;

import com.churchleague.championship.dto.StandingDTO;
import com.churchleague.championship.dto.TournamentDashboardDTO;
import com.churchleague.championship.model.Match;
import com.churchleague.championship.model.MatchStatus;
import com.churchleague.championship.model.Team;
import com.churchleague.championship.model.Tournament;
import com.churchleague.championship.repository.MatchRepository;
import com.churchleague.championship.repository.TeamRepository;
import com.churchleague.championship.repository.TournamentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository repo;
    private final TeamRepository teamRepo;
    private final MatchRepository matchRepo;
    private final MatchService matchService; // ✅ reutiliza lógica de próximos jogos

    // ===== CRUD =====
    public Tournament create(Tournament t) {
        return repo.save(t);
    }

    public List<Tournament> list() {
        return repo.findAll();
    }

    public Tournament get(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public Tournament update(Long id, Tournament t) {
        t.setId(id);
        return repo.save(t);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    // ===== INSCRIÇÃO DE TIMES =====
    @Transactional
    public Tournament addTeamToTournament(Long tournamentId, Long teamId) {
        Tournament t = repo.findById(tournamentId).orElseThrow();
        Team team = teamRepo.findById(teamId).orElseThrow();

        if (t.getTeams().contains(team)) {
            throw new IllegalStateException("Time já inscrito neste campeonato.");
        }

        t.getTeams().add(team);
        return repo.save(t);
    }

    @Transactional
    public Tournament removeTeamFromTournament(Long tournamentId, Long teamId) {
        Tournament t = repo.findById(tournamentId).orElseThrow();
        Team team = teamRepo.findById(teamId).orElseThrow();

        t.getTeams().remove(team);
        return repo.save(t);
    }

    public List<Team> listTeamsFromTournament(Long tournamentId) {
        Tournament t = repo.findById(tournamentId).orElseThrow();
        return new ArrayList<>(t.getTeams());
    }

    // ===== CLASSIFICAÇÃO =====
    public List<StandingDTO> getStandings(Long tournamentId) {
        Tournament t = repo.findById(tournamentId).orElseThrow();

        // pega todos os jogos desse torneio
        List<Match> matches = matchRepo.findByTournamentOrderByRoundNumberAsc(t);

        // timeId -> dados da classificação
        Map<Long, StandingDTO> standings = new HashMap<>();

        // inicializa com os times inscritos (todo mundo aparece mesmo sem jogar)
        for (Team team : t.getTeams()) {
            StandingDTO dto = StandingDTO.builder()
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
            standings.put(team.getId(), dto);
        }

        // percorre jogos finalizados e soma
        for (Match m : matches) {
            if (m.getStatus() != MatchStatus.FINALIZADO) {
                continue;
            }

            Long homeId = m.getHomeTeam().getId();
            Long awayId = m.getAwayTeam().getId();

            StandingDTO home = standings.get(homeId);
            StandingDTO away = standings.get(awayId);

            // se algum jogo envolver time não inscrito, garante que ele entra
            if (home == null) {
                home = StandingDTO.builder()
                        .teamId(m.getHomeTeam().getId())
                        .teamName(m.getHomeTeam().getName())
                        .build();
                standings.put(homeId, home);
            }
            if (away == null) {
                away = StandingDTO.builder()
                        .teamId(m.getAwayTeam().getId())
                        .teamName(m.getAwayTeam().getName())
                        .build();
                standings.put(awayId, away);
            }

            int hg = m.getHomeGoals() != null ? m.getHomeGoals() : 0;
            int ag = m.getAwayGoals() != null ? m.getAwayGoals() : 0;

            // jogos
            home.setPlayed(home.getPlayed() + 1);
            away.setPlayed(away.getPlayed() + 1);

            // gols
            home.setGoalsFor(home.getGoalsFor() + hg);
            home.setGoalsAgainst(home.getGoalsAgainst() + ag);

            away.setGoalsFor(away.getGoalsFor() + ag);
            away.setGoalsAgainst(away.getGoalsAgainst() + hg);

            // saldo (recalcula)
            home.setGoalDifference(home.getGoalsFor() - home.getGoalsAgainst());
            away.setGoalDifference(away.getGoalsFor() - away.getGoalsAgainst());

            // pontos conforme regras do torneio
            int winPoints = t.getPointsWin();
            int drawPoints = t.getPointsDraw();
            int lossPoints = t.getPointsLoss();

            if (hg > ag) {
                home.setWins(home.getWins() + 1);
                home.setPoints(home.getPoints() + winPoints);

                away.setLosses(away.getLosses() + 1);
                away.setPoints(away.getPoints() + lossPoints);
            } else if (hg < ag) {
                away.setWins(away.getWins() + 1);
                away.setPoints(away.getPoints() + winPoints);

                home.setLosses(home.getLosses() + 1);
                home.setPoints(home.getPoints() + lossPoints);
            } else {
                home.setDraws(home.getDraws() + 1);
                away.setDraws(away.getDraws() + 1);

                home.setPoints(home.getPoints() + drawPoints);
                away.setPoints(away.getPoints() + drawPoints);
            }
        }

        List<StandingDTO> result = standings.values().stream()
                .sorted(
                        Comparator
                                .comparingInt(StandingDTO::getPoints).reversed()
                                .thenComparingInt(StandingDTO::getGoalDifference).reversed()
                                .thenComparingInt(StandingDTO::getGoalsAgainst) // menos gols sofridos melhor
                                .thenComparingInt(StandingDTO::getGoalsFor).reversed()
                                .thenComparing(StandingDTO::getTeamName)
                )
                .toList();

        int pos = 1;
        for (StandingDTO s : result) {
            s.setPosition(pos++);
        }

        return result;
    }

    public TournamentDashboardDTO buildDashboard(Long tournamentId) {
        Tournament t = repo.findById(tournamentId).orElseThrow();

        List<Team> teams = new ArrayList<>(t.getTeams());
        List<StandingDTO> standings = getStandings(tournamentId);
        List<Match> nextMatches = matchService.listNextMatches(tournamentId);

        return TournamentDashboardDTO.builder()
                .tournament(t)
                .teams(teams)
                .standings(standings)
                .nextMatches(nextMatches)
                .build();
    }
}
