package de.rwth.matchday.lineup.domain;

import java.util.HashSet;
import java.util.List;

/**
 * Aggregate root of the Lineup bounded context.
 * External aggregates (Match, Team, Player) are referenced only by their IDs.
 */
public final class Lineup {
    private final LineupId id;
    private final MatchId matchId;
    private final TeamId teamId;
    private final List<PlayerId> playerIds;
    private LineupStatus status;

    public Lineup(MatchId matchId, TeamId teamId, List<PlayerId> playerIds) {
        this(new LineupId(), matchId, teamId, playerIds);
    }

    public Lineup(LineupId id, MatchId matchId, TeamId teamId, List<PlayerId> playerIds) {
        if (id == null || matchId == null || teamId == null)
            throw new IllegalArgumentException("lineupId, matchId and teamId are required");
        if (playerIds == null || playerIds.isEmpty() || playerIds.stream().anyMatch(p -> p == null))
            throw new IllegalArgumentException("A lineup requires at least one valid player");
        if (new HashSet<>(playerIds).size() != playerIds.size())
            throw new IllegalArgumentException("A player cannot appear twice in one lineup");

        this.id = id;
        this.matchId = matchId;
        this.teamId = teamId;
        this.playerIds = List.copyOf(playerIds);
        this.status = LineupStatus.PROPOSED;
    }

    public LineupId getId() { return id; }
    public MatchId getMatchId() { return matchId; }
    public TeamId getTeamId() { return teamId; }
    public List<PlayerId> getPlayerIds() { return playerIds; }
    public synchronized LineupStatus getStatus() { return status; }

    public synchronized void confirm() {
        requireProposed();
        status = LineupStatus.CONFIRMED;
    }

    public synchronized void reject() {
        requireProposed();
        status = LineupStatus.REJECTED;
    }

    private void requireProposed() {
        if (status != LineupStatus.PROPOSED)
            throw new IllegalStateException("Only a proposed lineup can be confirmed or rejected");
    }
}

