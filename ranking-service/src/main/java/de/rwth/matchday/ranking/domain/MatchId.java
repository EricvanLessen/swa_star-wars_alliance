package de.rwth.matchday.ranking.domain;

import java.util.UUID;

public record MatchId(UUID value) {
    public MatchId {
        if (value == null) throw new IllegalArgumentException("matchId is required");
    }
}

