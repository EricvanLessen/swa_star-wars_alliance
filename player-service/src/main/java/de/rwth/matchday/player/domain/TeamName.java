package de.rwth.matchday.player.domain;

import java.util.Objects;

public record TeamName(String name) {
    public TeamName {
        Objects.requireNonNull(name, "TeamName must not be null");
    }
    @Override
    public String toString() {
        return name;
    }
}
