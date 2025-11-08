package com.churchleague.championship.dto;

import com.churchleague.championship.model.MatchStatus;
import lombok.Data;

import java.util.List;

@Data
public class MatchResultDTO {

    private Integer homeGoals;
    private Integer awayGoals;

    private MatchStatus status = MatchStatus.FINALIZADO;

    private List<Long> homeScorers;
    private List<Long> awayScorers;
}
