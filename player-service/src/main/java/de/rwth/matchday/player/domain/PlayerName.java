package de.rwth.matchday.player.domain;

import java.util.Objects;

/**
 * Object Value
 * @param name player name
 */
public record PlayerName(String name) {
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 20;

    public PlayerName {
        Objects.requireNonNull(name, "PlayerName must not be null");
        name = name.strip();

        if (name.length() < MIN_LENGTH || name.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "PlayerName must be %d-%d characters long".formatted(MIN_LENGTH, MAX_LENGTH));
        }
    }
    @Override
    public String toString() {
        return name;
    }
}
