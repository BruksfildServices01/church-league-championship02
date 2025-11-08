package com.churchleague.championship.repository;

import com.churchleague.championship.model.GoalEvent;
import com.churchleague.championship.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GoalEventRepository extends JpaRepository<GoalEvent, Long> {

    void deleteByMatch(Match match);

    // Artilharia por torneio (só conta jogos FINALIZADOS)
    @Query("""
        select ge.player.id as playerId,
               ge.player.name as playerName,
               ge.player.team.id as teamId,
               ge.player.team.name as teamName,
               count(ge.id) as goals
        from GoalEvent ge
        where ge.match.tournament.id = :tournamentId
          and ge.match.status = com.churchleague.championship.model.MatchStatus.FINALIZADO
        group by ge.player.id, ge.player.name, ge.player.team.id, ge.player.team.name
        order by goals desc, playerName asc
    """)
    List<Object[]> aggregateTournamentScorers(Long tournamentId);

    // Artilharia de uma partida específica
    @Query("""
        select ge.player.id as playerId,
               ge.player.name as playerName,
               ge.player.team.id as teamId,
               ge.player.team.name as teamName,
               count(ge.id) as goals
        from GoalEvent ge
        where ge.match.id = :matchId
        group by ge.player.id, ge.player.name, ge.player.team.id, ge.player.team.name
        order by goals desc, playerName asc
    """)
    List<Object[]> aggregateMatchScorers(Long matchId);

    // (Opcional) Artilharia global (todas as partidas, qualquer torneio/estado)
    @Query("""
        select ge.player.id as playerId,
               ge.player.name as playerName,
               ge.player.team.id as teamId,
               ge.player.team.name as teamName,
               count(ge.id) as goals
        from GoalEvent ge
        group by ge.player.id, ge.player.name, ge.player.team.id, ge.player.team.name
        order by goals desc, playerName asc
    """)
    List<Object[]> aggregateGlobalScorers();
}
