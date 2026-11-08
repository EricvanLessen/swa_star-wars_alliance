package de.rwth.matchday.ranking.domain;

import java.util.UUID;

public record TeamId(UUID value) {
    public TeamId {
        if (value == null) throw new IllegalArgumentException("teamId is required");
    }
}

