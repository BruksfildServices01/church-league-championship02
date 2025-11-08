package com.churchleague.championship.controller;

import com.churchleague.championship.dto.MatchResultDTO;
import com.churchleague.championship.dto.ScorerDTO;
import com.churchleague.championship.model.Match;
import com.churchleague.championship.model.MatchStatus;
import com.churchleague.championship.model.WinnerSide;
import com.churchleague.championship.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService service;

    // 1) GERAR JOGOS
    @PostMapping("/generate/{tournamentId}")
    public List<Match> generate(@PathVariable Long tournamentId,
                                @RequestParam(defaultValue = "false") boolean hasReturn) {
        try {
            return service.generateRoundRobin(tournamentId, hasReturn);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // 2) REGERAR
    @PostMapping("/tournament/{tournamentId}/regenerate")
    public List<Match> regenerate(@PathVariable Long tournamentId,
                                  @RequestParam(defaultValue = "false") boolean hasReturn,
                                  @RequestParam(defaultValue = "false") boolean force) {
        try {
            return service.regenerateSchedule(tournamentId, hasReturn, force);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // 3) LISTAR JOGOS DO TORNEIO (opcional rodada via query param)
    @GetMapping("/tournament/{tournamentId}")
    public List<Match> listByTournament(@PathVariable Long tournamentId,
                                        @RequestParam(required = false) Integer round) {
        if (round != null) {
            return service.listByTournamentAndRound(tournamentId, round);
        }
        return service.listByTournament(tournamentId);
    }

    // 3.1) ROTA POR RODADA
    @GetMapping("/tournament/{tournamentId}/round/{round}")
    public List<Match> listByRound(@PathVariable Long tournamentId,
                                   @PathVariable Integer round) {
        return service.listByTournamentAndRound(tournamentId, round);
    }

    // 3.2) FILTRO GENÉRICO POR QUERY PARAMS (único handler de GET /api/matches)
    // Ex.: /api/matches?tournamentId=1&status=FINALIZADO
    @GetMapping
    public List<Match> listMatches(@RequestParam(required = false) Long tournamentId,
                                   @RequestParam(required = false) MatchStatus status) {
        if (tournamentId != null && status != null) {
            return service.listByTournamentAndStatus(tournamentId, status);
        } else if (tournamentId != null) {
            return service.listByTournament(tournamentId);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Informe ao menos tournamentId (e opcionalmente status).");
        }
    }

    // 4) ATUALIZAR RESULTADO SIMPLES
    @PutMapping("/{matchId}/result")
    public Match updateResultSimple(@PathVariable Long matchId,
                                    @RequestParam Integer homeGoals,
                                    @RequestParam Integer awayGoals,
                                    @RequestParam(defaultValue = "FINALIZADO") MatchStatus status) {
        try {
            return service.updateResult(matchId, homeGoals, awayGoals, status);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    // 5) ATUALIZAR RESULTADO COM ARTILHEIROS
    @PutMapping("/{matchId}/result-with-scorers")
    public Match updateResultWithScorers(@PathVariable Long matchId,
                                         @RequestBody MatchResultDTO dto) {
        try {
            return service.updateResultWithScorers(
                    matchId,
                    dto.getHomeGoals(),
                    dto.getAwayGoals(),
                    dto.getHomeScorers(),
                    dto.getAwayScorers(),
                    dto.getStatus()
            );
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    // 6) MARCAR WO
    @PutMapping("/{matchId}/wo")
    public Match applyWO(@PathVariable Long matchId,
                         @RequestParam(defaultValue = "HOME") WinnerSide winnerSide) {
        try {
            return service.applyWO(matchId, winnerSide.name());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }


    // 7) PRÓXIMOS JOGOS
    @GetMapping("/tournament/{tournamentId}/next")
    public List<Match> listNext(@PathVariable Long tournamentId) {
        return service.listNextMatches(tournamentId);
    }

    // 8) FINALIZADOS
    @GetMapping("/tournament/{tournamentId}/finished")
    public List<Match> listFinished(@PathVariable Long tournamentId) {
        return service.listByTournamentAndStatus(tournamentId, MatchStatus.FINALIZADO);
    }

    // 9) AGENDAR DATA/LOCAL
    @PutMapping("/{matchId}/schedule")
    public Match schedule(@PathVariable Long matchId,
                          @RequestBody Map<String, String> body) {
        try {
            return service.schedule(matchId, body.get("matchDate"), body.get("location"));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    // 10) ARTILHARIA DE UMA PARTIDA
    @GetMapping("/{matchId}/scorers")
    public List<ScorerDTO> matchScorers(@PathVariable Long matchId) {
        return service.matchScorers(matchId);
    }
}
