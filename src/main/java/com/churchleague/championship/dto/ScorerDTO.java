package com.churchleague.championship.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScorerDTO {
    private Long playerId;
    private String playerName;
    private Long teamId;
    private String teamName;
    private Long goals;
}
