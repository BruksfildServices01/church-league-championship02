package com.churchleague.championship.controller;

import com.churchleague.championship.dto.ScorerDTO;
import com.churchleague.championship.model.Player;
import com.churchleague.championship.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    // criar jogador já amarrado ao time
    @PostMapping("/team/{teamId}")
    public Player createForTeam(@PathVariable Long teamId, @RequestBody Player body) {
        return playerService.create(teamId, body);
    }

    // listar jogadores de um time
    @GetMapping("/team/{teamId}")
    public List<Player> listByTeam(@PathVariable Long teamId) {
        return playerService.listByTeam(teamId);
    }

    // ranking geral
    @GetMapping("/ranking")
    public List<ScorerDTO> ranking() {        // <-- muda o retorno
        return playerService.topScorersFromEvents();
    }

    // adicionar gol manual (teste)
    @PostMapping("/{playerId}/goal")
    public Player addGoal(@PathVariable Long playerId) {
        return playerService.addGoal(playerId);
    }


}
