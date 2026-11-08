package de.rwth.matchday.lineup.domain;

import java.util.UUID;

public record LineupId(UUID value) {
    public LineupId {
        if (value == null) throw new IllegalArgumentException("lineupId is required");
    }
    public LineupId() { this(UUID.randomUUID()); }
}

