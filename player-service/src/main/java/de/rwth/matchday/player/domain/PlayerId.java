package de.rwth.matchday.player.domain;

import java.util.UUID;

/**
 * Value Object
 */
public record PlayerId(UUID playerId) {

    public PlayerId() {
        this(UUID.randomUUID());
    }

    public PlayerId(String playerId) {
        this(UUID.fromString(playerId));
    }

    @Override
    public String toString() {
        return playerId.toString();
    }
}
