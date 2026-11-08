package de.rwth.matchday.match.domain;

import java.util.UUID;

/**
 * Value Object - DTO perhaps this is only temporary
 */
public record MatchId(UUID matchId) {

    public MatchId() {
        this(UUID.randomUUID());
    }

    public MatchId(String matchId) {
        this(UUID.fromString(matchId));
    }

    @Override
    public String toString() {
        return matchId.toString();
    }
}
