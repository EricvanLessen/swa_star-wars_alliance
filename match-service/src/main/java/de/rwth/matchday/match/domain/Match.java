package de.rwth.matchday.match.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Match aggregate for scheduling, recording and confirming a team result. */
public class Match {
    private final MatchId id;
    private final TeamId teamId;
    private final String teamName;
    private final String season;
    private final LocalDate date;
    private final String opponentTeamName;
    private final Venue venue;
    private final List<IndividualMatch> matches = new ArrayList<>();
    private Integer ownGames;
    private Integer opponentGames;
    private boolean confirmed;

    public Match(LocalDate date, String opponentTeamName, TeamId teamId, Venue venue) {
        this(new MatchId(), date, opponentTeamName, teamId, venue, teamId.toString(), "unspecified");
    }
    public Match(MatchId id, LocalDate date, String opponentTeamName,
                 TeamId teamId, Venue venue, List<IndividualMatch> matches) {
        this(id, date, opponentTeamName, teamId, venue, teamId.toString(), "unspecified");
        this.matches.addAll(matches);
    }
    public Match(MatchId id, LocalDate date, String opponentTeamName,
                 TeamId teamId, Venue venue, String teamName, String season) {
        if (id == null || date == null || teamId == null || venue == null)
            throw new IllegalArgumentException("id, date, teamId and venue required");
        if (opponentTeamName == null || opponentTeamName.isBlank() || teamName == null || teamName.isBlank())
            throw new IllegalArgumentException("Both team names required");
        if (season == null || season.isBlank())
            throw new IllegalArgumentException("season required");
        this.id = id;
        this.date = date;
        this.opponentTeamName = opponentTeamName.trim();
        this.teamId = teamId;
        this.venue = venue;
        this.teamName = teamName.trim();
        this.season = season.trim();
    }
    public MatchId getId() { return id; }
    public TeamId getTeamId() { return teamId; }
    public String getTeamName() { return teamName; }
    public String getSeason() { return season; }
    public LocalDate getDate() { return date; }
    public String getOpponentTeamName() { return opponentTeamName; }
    public Venue getVenue() { return venue; }
    public List<IndividualMatch> getMatches() { return List.copyOf(matches); }
    public Integer getOwnGames() { return ownGames; }
    public Integer getOpponentGames() { return opponentGames; }
    public boolean isConfirmed() { return confirmed; }

    public void addMatch(IndividualMatch match) {
        if (matches.size() >= 10) throw new IllegalStateException("Maximum 10 individual matches");
        matches.add(match);
    }
    public synchronized void recordResult(int ownGames, int opponentGames) {
        if (ownGames < 0 || opponentGames < 0 || ownGames + opponentGames == 0)
            throw new IllegalArgumentException("Results must be nonnegative and nonempty");
        this.ownGames = ownGames;
        this.opponentGames = opponentGames;
        this.confirmed = false; // corrected scores require confirmation again
    }
    public synchronized void confirmResult() {
        if (ownGames == null || opponentGames == null)
            throw new IllegalStateException("Record a result before confirmation");
        confirmed = true;
    }
    public synchronized MatchResult result() {
        if (ownGames == null || opponentGames == null) return MatchResult.PENDING;
        if (ownGames > opponentGames) return MatchResult.OWN_WIN;
        if (opponentGames > ownGames) return MatchResult.OPPONENT_WIN;
        return MatchResult.DRAW;
    }
    public synchronized MatchResult DidOurTeamWin() {
        if (ownGames != null) return result();
        int wins = 0, losses = 0;
        for (IndividualMatch match : matches) {
            if (match.didOurTeamWin()) wins++;
            else losses++;
        }
        if (wins + losses == 0) return MatchResult.PENDING;
        if (wins > losses) return MatchResult.OWN_WIN;
        if (losses > wins) return MatchResult.OPPONENT_WIN;
        return MatchResult.DRAW;
    }
}

