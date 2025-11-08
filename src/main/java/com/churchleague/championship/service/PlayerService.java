package com.churchleague.championship.service;

import com.churchleague.championship.dto.ScorerDTO;
import com.churchleague.championship.model.Player;
import com.churchleague.championship.model.Team;
import com.churchleague.championship.repository.GoalEventRepository;
import com.churchleague.championship.repository.PlayerRepository;
import com.churchleague.championship.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepo;
    private final TeamRepository teamRepo;
    private final GoalEventRepository goalEventRepo;

    /** Cria um jogador já vinculado ao time informado. */
    public Player create(Long teamId, Player dto) {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Time não encontrado"));

        String name = (dto.getName() == null ? "" : dto.getName().trim());
        if (name.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do jogador é obrigatório");
        }

        if (playerRepo.existsByNameIgnoreCaseAndTeam(name, team)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Jogador já existe neste time");
        }

        Player p = Player.builder()
                .name(name)
                .team(team)
                .goals(0) // campo legado; mantido para testes/compatibilidade
                .build();

        return playerRepo.save(p);
    }

    /** Lista os jogadores de um time. */
    public List<Player> listByTeam(Long teamId) {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Time não encontrado"));
        return playerRepo.findByTeam(team);
    }

    // =========================================================================
    // ============ NOVO RANKING (fonte da verdade = GoalEvent) ===============
    // =========================================================================

    /** Ranking global de artilharia a partir dos GoalEvents (recomendado). */
    public List<ScorerDTO> topScorersFromEvents() {
        return goalEventRepo.aggregateGlobalScorers().stream()
                .map(r -> new ScorerDTO(
                        (Long)   r[0], // playerId
                        (String) r[1], // playerName
                        (Long)   r[2], // teamId
                        (String) r[3], // teamName
                        (Long)   r[4]  // goals
                ))
                .toList();
    }

    // =========================================================================
    // ====================== MÉTODOS LEGADOS (opcionais) ======================
    // =========================================================================

    /**
     * Soma 1 gol no campo legado Player.goals.
     * Útil apenas para testes manuais; não afeta o ranking por eventos.
     */
    @Deprecated
    public Player addGoal(Long playerId) {
        Player p = playerRepo.findById(playerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado"));
        p.setGoals(p.getGoals() + 1);
        return playerRepo.save(p);
    }

    /**
     * Ranking legado baseado no campo Player.goals.
     * Prefira {@link #topScorersFromEvents()} no código novo.
     */
    @Deprecated
    public List<Player> topScorers() {
        return playerRepo.findAll().stream()
                .sorted(Comparator.comparingInt(Player::getGoals).reversed()
                        .thenComparing(Player::getName))
                .toList();
    }
}
