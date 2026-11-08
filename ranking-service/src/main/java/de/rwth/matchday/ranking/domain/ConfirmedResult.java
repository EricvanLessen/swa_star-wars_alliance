package de.rwth.matchday.ranking.domain;

/**
 * Immutable snapshot of a confirmed result received from an upstream service.
 * Ranking does not own the match or decide when its confirmation is valid.
 */
public record ConfirmedResult(MatchId matchId, String season, TeamId teamId, MatchOutcome outcome) {
    public ConfirmedResult {
        if (matchId == null || teamId == null)
            throw new IllegalArgumentException("matchId and teamId are required");
        if (season == null || season.isBlank())
            throw new IllegalArgumentException("season is required");
        season = season.trim();
        if (outcome == null || outcome == MatchOutcome.PENDING)
            throw new IllegalArgumentException("Only a completed, confirmed match result may be ranked");
    }
}

