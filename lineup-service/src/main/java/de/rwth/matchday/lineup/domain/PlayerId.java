package de.rwth.matchday.lineup.domain;

import java.util.UUID;

public record PlayerId(UUID value) {
    public PlayerId {
        if (value == null) throw new IllegalArgumentException("playerId is required");
    }
}

