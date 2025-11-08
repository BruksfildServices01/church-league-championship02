package com.churchleague.championship.service;

import com.churchleague.championship.model.Team;
import com.churchleague.championship.repository.TeamRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }

    // POST (criação)
    public Team save(Team team) {
        // 1) normaliza a sigla
        String sigla = team.getSigla();
        if (sigla == null || sigla.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sigla é obrigatória");
        }

        sigla = sigla.trim().toUpperCase();
        team.setSigla(sigla);

        // 2) checa duplicidade ignorando maiúsculas
        if (teamRepository.existsBySiglaIgnoreCase(sigla)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sigla já cadastrada");
        }

        return teamRepository.save(team);
    }

    // PUT (atualização)
    // TeamService.java
    @Transactional
    public Team update(Long id, Team dto) {
        Team existing = teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Time não encontrado"));

        String sigla = dto.getSigla();
        if (sigla == null || sigla.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sigla é obrigatória");
        }

        sigla = sigla.trim().toUpperCase();

        if (teamRepository.existsBySiglaIgnoreCaseAndIdNot(sigla, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sigla já cadastrada para outro time");
        }

        existing.setName(dto.getName());
        existing.setSigla(sigla);
        existing.setCaptain(dto.getCaptain());
        existing.setPhone(dto.getPhone());
        existing.setStatus(dto.getStatus());
        existing.setNotes(dto.getNotes());

        // ⚠️ NÃO faça: existing.setPlayers(dto.getPlayers());

        return teamRepository.save(existing);
    }


    public void delete(Long id) {
        teamRepository.deleteById(id);
    }
}
