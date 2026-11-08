package de.rwth.matchday.match.domain;

import java.util.Objects;

public record OpponentPlayer(String name) {
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 20;
    public OpponentPlayer{
            Objects.requireNonNull(name, "PlayerName must not be null");
            name = name.strip();

            if (name.length() < MIN_LENGTH || name.length() > MAX_LENGTH) {
                throw new IllegalArgumentException(
                        "PlayerName must be %d-%d characters long".formatted(MIN_LENGTH, MAX_LENGTH));
            }
        }

}