package com.churchleague.championship.repository;

import com.churchleague.championship.model.Player;
import com.churchleague.championship.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByTeam(Team team);

    boolean existsByNameIgnoreCaseAndTeam(String name, Team team);
}
