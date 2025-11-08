package com.churchleague.championship.repository;

import com.churchleague.championship.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournament, Long> { }