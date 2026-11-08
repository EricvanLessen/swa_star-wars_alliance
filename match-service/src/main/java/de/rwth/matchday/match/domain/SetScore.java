package de.rwth.matchday.match.domain;

import java.util.Objects;

public record SetScore(int ownPoints, int opponentPoints) {

    private static final int POINTS_TO_WIN = 11;
    private static final int MIN_LEAD = 2;

    public SetScore {
        if (ownPoints < 0 || opponentPoints < 0) {
            throw new IllegalArgumentException("Points must not be negative: " + ownPoints + ":" + opponentPoints);
        }

        int winnerPoints = Math.max(ownPoints, opponentPoints);
        int loserPoints = Math.min(ownPoints, opponentPoints);

        // Regular finish: 11:0 up to 11:9
        boolean regularWin = winnerPoints == POINTS_TO_WIN
                && winnerPoints - loserPoints >= MIN_LEAD;

        // Extended finish after 10:10: 12:10, 13:11, 14:12, ...
        boolean extendedWin = winnerPoints > POINTS_TO_WIN
                && winnerPoints - loserPoints == MIN_LEAD;

        if (!regularWin && !extendedWin) {
            throw new IllegalArgumentException(
                    "Invalid set score: " + ownPoints + ":" + opponentPoints);
        }
    }

    public boolean didOurTeamWin() {
        return ownPoints > opponentPoints;
    }

    public static SetScore parse(String value) {
        Objects.requireNonNull(value, "value must not be null");
        String[] parts = value.strip().split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Expected format '<ownPoints>:<opponentPoints>', got: " + value);
        }
        try {
            return new SetScore(Integer.parseInt(parts[0].strip()), Integer.parseInt(parts[1].strip()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Not a number in set score: " + value, e);
        }
    }

    @Override
    public String toString() {
        return ownPoints + ":" + opponentPoints;
    }
}
