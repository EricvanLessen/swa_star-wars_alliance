package de.rwth.matchday.llm.domain;

import java.time.LocalDate;
import java.util.List;

/**
 * Input snapshot for report generation. Match Service will eventually provide
 * this data via a dedicated adapter; no cross-context Java objects are shared.
 */
public record MatchReportInput(
        String team, String opponent, LocalDate date,
        int ownGames, int opponentGames, List<String> highlights) {

    public MatchReportInput {
        if (team == null || team.isBlank() || opponent == null || opponent.isBlank() || date == null)
            throw new IllegalArgumentException("team, opponent and date are required");
        if (ownGames < 0 || opponentGames < 0 || ownGames + opponentGames == 0)
            throw new IllegalArgumentException("Match results must contain non-negative, non-empty game scores");
        team = team.trim();
        opponent = opponent.trim();
        if (highlights == null) highlights = List.of();
        if (highlights.stream().anyMatch(s -> s == null || s.isBlank()))
            throw new IllegalArgumentException("highlights cannot contain blank values");
        highlights = List.copyOf(highlights);
    }
}

