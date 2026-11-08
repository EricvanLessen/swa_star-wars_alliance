package de.rwth.matchday.match.domain;

import java.util.UUID;

/**
 * Value Object
 */
public record TeamId(UUID teamId) {

    public TeamId() {
        this(UUID.randomUUID());
    }
    public TeamId(UUID teamId) {
        this.teamId = teamId;
    }
    public boolean isTeam() {
        return teamId != null;
    }
    public TeamId(String teamId) {
        this(UUID.fromString(teamId));
    }
    @Override
    public String toString() {
        return teamId.toString();
    }
}
