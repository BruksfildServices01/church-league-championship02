package com.churchleague.championship.service;

import com.churchleague.championship.dto.MatchResultDTO;
import com.churchleague.championship.dto.ScorerDTO;
import com.churchleague.championship.model.*;
import com.churchleague.championship.repository.MatchRepository;
import com.churchleague.championship.repository.PlayerRepository;
import com.churchleague.championship.repository.TeamRepository;
import com.churchleague.championship.repository.TournamentRepository;

// >>> NOVOS IMPORTS
import com.churchleague.championship.model.GoalEvent;
import com.churchleague.championship.repository.GoalEventRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepo;
    private final TournamentRepository tournamentRepo;
    private final TeamRepository teamRepo;
    private final PlayerRepository playerRepo;

    // >>> NOVO: repo de eventos de gol
    private final GoalEventRepository goalEventRepo;

    // ===== LISTAGENS =====

    public List<Match> listByTournament(Long tournamentId) {
        Tournament t = tournamentRepo.findById(tournamentId).orElseThrow();
        return matchRepo.findByTournamentOrderByRoundNumberAsc(t);
    }

    public List<Match> listByTournamentAndStatus(Long tournamentId, MatchStatus status) {
        Tournament t = tournamentRepo.findById(tournamentId).orElseThrow();
        return matchRepo.findByTournamentAndStatusOrderByRoundNumberAsc(t, status);
    }

    public List<Match> listByTournamentAndRound(Long tournamentId, int round) {
        Tournament t = tournamentRepo.findById(tournamentId).orElseThrow();
        return matchRepo.findByTournamentAndRoundNumberOrderByRoundNumberAsc(t, round);
    }

    // ===== GERAR TABELA =====
    @Transactional
    public List<Match> generateRoundRobin(Long tournamentId, boolean hasReturn) {
        Tournament t = tournamentRepo.findById(tournamentId).orElseThrow();

        List<Team> teams = new ArrayList<>();
        if (t.getTeams() != null && !t.getTeams().isEmpty()) {
            teams.addAll(t.getTeams());
        }

        if (teams.size() < 2) {
            throw new IllegalStateException("Cadastre ao menos 2 times (ou inscreva-os no campeonato) para gerar a tabela.");
        }

        List<Team> rotating = new ArrayList<>(teams);

        if (rotating.size() % 2 != 0) {
            rotating.add(null);
        }

        int n = rotating.size();
        int rounds = n - 1;
        List<Match> allMatches = new ArrayList<>();

        for (int r = 0; r < rounds; r++) {
            for (int i = 0; i < n / 2; i++) {
                Team a = rotating.get(i);
                Team b = rotating.get(n - 1 - i);

                if (a != null && b != null) {
                    boolean evenRound = (r % 2 == 0);
                    Team home = evenRound ? a : b;
                    Team away = evenRound ? b : a;

                    Match m = Match.builder()
                            .tournament(t)
                            .roundNumber(r + 1)
                            .homeTeam(home)
                            .awayTeam(away)
                            .status(MatchStatus.AGENDADO)
                            .build();

                    allMatches.add(m);
                }
            }

            List<Team> next = new ArrayList<>();
            next.add(rotating.get(0));
            next.add(rotating.get(n - 1));
            next.addAll(rotating.subList(1, n - 1));
            rotating = next;
        }

        if (hasReturn) {
            int baseRound = rounds;
            List<Match> returnLeg = new ArrayList<>();
            for (Match m : allMatches) {
                Match rev = Match.builder()
                        .tournament(t)
                        .roundNumber(m.getRoundNumber() + baseRound)
                        .homeTeam(m.getAwayTeam())
                        .awayTeam(m.getHomeTeam())
                        .status(MatchStatus.AGENDADO)
                        .build();
                returnLeg.add(rev);
            }
            allMatches.addAll(returnLeg);
        }

        List<Match> old = matchRepo.findByTournamentOrderByRoundNumberAsc(t);
        for (Match m : old) {
            goalEventRepo.deleteByMatch(m);
            matchRepo.delete(m);
        }


        return matchRepo.saveAll(allMatches);
    }

    // ===== LANÇAR RESULTADO SIMPLES =====

    public Match updateResult(Long matchId,
                              Integer homeGoals,
                              Integer awayGoals,
                              MatchStatus status) {
        Match m = matchRepo.findById(matchId).orElseThrow();

        if (m.getStatus() == MatchStatus.FINALIZADO) {
            throw new IllegalStateException("Este jogo já foi finalizado.");
        }

        m.setHomeGoals(homeGoals);
        m.setAwayGoals(awayGoals);
        m.setStatus(status != null ? status : MatchStatus.FINALIZADO);

        return matchRepo.save(m);
    }

    // ===== LANÇAR RESULTADO + ARTILHEIROS (ATUALIZADO) =====
    @Transactional
    public Match updateResultWithScorers(Long matchId,
                                         Integer homeGoals,
                                         Integer awayGoals,
                                         List<Long> homeScorers,
                                         List<Long> awayScorers,
                                         MatchStatus status) {

        Match m = matchRepo.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Partida não encontrada."));

        if (homeGoals == null) homeGoals = 0;
        if (awayGoals == null) awayGoals = 0;

        // valida consistência artilheiros x gols
        if (homeScorers != null && homeScorers.size() != homeGoals) {
            throw new IllegalArgumentException("Quantidade de artilheiros do mandante não bate com o número de gols.");
        }
        if (awayScorers != null && awayScorers.size() != awayGoals) {
            throw new IllegalArgumentException("Quantidade de artilheiros do visitante não bate com o número de gols.");
        }

        boolean jaFinalizado = m.getStatus() == MatchStatus.FINALIZADO;

        if (jaFinalizado) {
            // se já está finalizado, só permitimos atualizar artilheiros se o placar enviado for idêntico
            int hgAtual = m.getHomeGoals() != null ? m.getHomeGoals() : 0;
            int agAtual = m.getAwayGoals() != null ? m.getAwayGoals() : 0;

            boolean mesmoPlacar = (hgAtual == homeGoals) && (agAtual == awayGoals);
            if (!mesmoPlacar) {
                throw new IllegalStateException("Este jogo já foi finalizado com outro placar.");
            }
            // mantém status FINALIZADO
        } else {
            // seta placar e status (padrão FINALIZADO se vier nulo)
            m.setHomeGoals(homeGoals);
            m.setAwayGoals(awayGoals);
            m.setStatus(status != null ? status : MatchStatus.FINALIZADO);
        }

        // limpa eventos antigos e recria com base nos IDs recebidos
        goalEventRepo.deleteByMatch(m);

        if (homeScorers != null) {
            for (Long pid : homeScorers) {
                Player p = playerRepo.findById(pid)
                        .orElseThrow(() -> new IllegalArgumentException("Artilheiro mandante inexistente: " + pid));
                if (!Objects.equals(p.getTeam().getId(), m.getHomeTeam().getId())) {
                    throw new IllegalArgumentException("Jogador " + pid + " não pertence ao time mandante.");
                }
                goalEventRepo.save(GoalEvent.builder().match(m).player(p).build());
            }
        }

        if (awayScorers != null) {
            for (Long pid : awayScorers) {
                Player p = playerRepo.findById(pid)
                        .orElseThrow(() -> new IllegalArgumentException("Artilheiro visitante inexistente: " + pid));
                if (!Objects.equals(p.getTeam().getId(), m.getAwayTeam().getId())) {
                    throw new IllegalArgumentException("Jogador " + pid + " não pertence ao time visitante.");
                }
                goalEventRepo.save(GoalEvent.builder().match(m).player(p).build());
            }
        }

        return matchRepo.save(m);
    }


    // ===== WO =====

    public Match applyWO(Long matchId, String winnerSide) {
        Match m = matchRepo.findById(matchId).orElseThrow();
        Tournament t = m.getTournament();

        // bloqueia estados inválidos
        if (m.getStatus() == MatchStatus.FINALIZADO) {
            throw new IllegalStateException("Não é possível aplicar W.O. em jogo finalizado.");
        }
        if (m.getStatus() == MatchStatus.CANCELADO) {
            throw new IllegalStateException("Não é possível aplicar W.O. em jogo cancelado.");
        }

        // normaliza e valida entrada
        String side = winnerSide == null ? "" : winnerSide.trim().toUpperCase();
        switch (side) {
            case "HOME", "MANDANTE", "H" -> {
                m.setHomeGoals(t.getWoHomeGoals());
                m.setAwayGoals(t.getWoAwayGoals());
            }
            case "AWAY", "VISITANTE", "A" -> {
                m.setHomeGoals(t.getWoAwayGoals());
                m.setAwayGoals(t.getWoHomeGoals());
            }
            default -> throw new IllegalArgumentException(
                    "Parâmetro winnerSide inválido. Use HOME/MANDANTE ou AWAY/VISITANTE.");
        }

        m.setStatus(MatchStatus.FINALIZADO);
        return matchRepo.save(m);
    }


    // ===== PRÓXIMOS JOGOS =====

    public List<Match> listNextMatches(Long tournamentId) {
        Tournament t = tournamentRepo.findById(tournamentId).orElseThrow();
        List<Match> all = matchRepo.findByTournamentOrderByRoundNumberAsc(t);

        LocalDate today = LocalDate.now();

        return all.stream()
                .filter(m -> m.getStatus() == MatchStatus.AGENDADO)
                .filter(m -> m.getMatchDate() == null || !m.getMatchDate().isBefore(today))
                .sorted(Comparator.comparing(Match::getMatchDate,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    // ===== AGENDAR =====
    @Transactional
    public Match schedule(Long matchId, String dateIso, String location) {
        Match m = matchRepo.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Partida não encontrada."));

        if (m.getStatus() == MatchStatus.CANCELADO) {
            throw new IllegalStateException("Não é possível agendar um jogo cancelado.");
        }
        if (m.getStatus() == MatchStatus.FINALIZADO) {
            throw new IllegalStateException("Não é possível reagendar um jogo já finalizado.");
        }

        if (dateIso != null && !dateIso.isBlank()) {
            try {
                m.setMatchDate(LocalDate.parse(dateIso));
            } catch (java.time.format.DateTimeParseException e) {
                throw new IllegalArgumentException("Data inválida. Use o formato yyyy-MM-dd.");
            }
        }

        if (location != null) {
            m.setLocation(location);
        }

        return matchRepo.save(m);
    }

    public List<ScorerDTO> tournamentScorers(Long tournamentId) {
        var rows = goalEventRepo.aggregateTournamentScorers(tournamentId);
        return rows.stream()
                .map(r -> new ScorerDTO(
                        (Long) r[0],
                        (String) r[1],
                        (Long) r[2],
                        (String) r[3],
                        (Long) r[4]
                ))
                .toList();
    }

    public List<ScorerDTO> matchScorers(Long matchId) {
        var rows = goalEventRepo.aggregateMatchScorers(matchId);
        return rows.stream()
                .map(r -> new ScorerDTO(
                        (Long) r[0],
                        (String) r[1],
                        (Long) r[2],
                        (String) r[3],
                        (Long) r[4]
                ))
                .toList();
    }

    @Transactional
    public List<Match> regenerateSchedule(Long tournamentId, boolean hasReturn, boolean force) {
        Tournament t = tournamentRepo.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneio não encontrado."));

        // Busca todos os jogos atuais
        List<Match> old = matchRepo.findByTournamentOrderByRoundNumberAsc(t);

        // Considera "travado" se FINALIZADO ou se já tem gols preenchidos
        boolean hasLocked = old.stream().anyMatch(m ->
                m.getStatus() == MatchStatus.FINALIZADO
                        || m.getHomeGoals() != null
                        || m.getAwayGoals() != null
        );

        if (hasLocked && !force) {
            throw new IllegalStateException(
                    "Já existem partidas com placar/status lançados. " +
                            "Para regerar mesmo assim, chame com ?force=true (os resultados serão perdidos)."
            );
        }

        // Limpa eventos de gol e remove os jogos antigos
        for (Match m : old) {
            goalEventRepo.deleteByMatch(m); // evita violação de FK
            matchRepo.delete(m);
        }

        // Gera nova tabela
        return generateRoundRobin(tournamentId, hasReturn);
    }

}
