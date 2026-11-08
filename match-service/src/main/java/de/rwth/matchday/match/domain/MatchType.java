package de.rwth.matchday.match.domain;

public enum MatchType {
    SINGLE(1), DOUBLE(2);

    private final int playersPerSide;
    MatchType(int n) { this.playersPerSide = n; }

    public int playersPerSide() { return playersPerSide; }
}