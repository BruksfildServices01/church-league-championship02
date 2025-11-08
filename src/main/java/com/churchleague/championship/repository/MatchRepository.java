package com.churchleague.championship.repository;

import com.churchleague.championship.model.Match;
import com.churchleague.championship.model.MatchStatus;
import com.churchleague.championship.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByTournamentOrderByRoundNumberAsc(Tournament tournament);

    List<Match> findByTournamentAndStatusOrderByRoundNumberAsc(Tournament tournament, MatchStatus status);

    List<Match> findByTournamentAndRoundNumberOrderByRoundNumberAsc(Tournament tournament, Integer roundNumber);
}