package de.rwth.matchday.match.domain;

import lombok.Getter;

import javax.naming.Binding;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate/Entity representing a Match
 */
@Getter
public class Match {
    private final MatchId id;
    private String opponentTeamName;
    private LocalDate date;
    private TeamId teamId;
    private Venue venue;
    private List<IndividualMatch> matches;

    public Match(LocalDate date, String opponentTeamName, TeamId teamId, Venue venue) {
        this.id = new MatchId();
        this.teamId = teamId;
        this.venue = venue;
        this.matches = new ArrayList<>();
        this.date = date;
        this.opponentTeamName = opponentTeamName;
    }
    public Match(MatchId id, LocalDate date, String opponentTeamName, TeamId teamId, Venue venue,  List<IndividualMatch> matches) {
        this.id = id;
        this.teamId = teamId;
        this.venue = venue;
        this.matches = new ArrayList<>();
        this.date = date;
        this.opponentTeamName = opponentTeamName;
        this.matches.addAll(matches);
    }
    public void addMatch(IndividualMatch match) {
        if(this.matches.size() > 9){
            throw new IllegalStateException("Too many individual matches!");
        }
        this.matches.add(match);
    }
    public MatchResult DidOurTeamWin() {
        int wins = 0;
        int losses = 0;
        for (IndividualMatch match : matches) {
            if (match.didOurTeamWin()) {
                wins++;
            } else {
                losses++;
            }
        }
        int diff = wins-losses;
        if(wins >5 || losses >5 || (wins == 5 && losses == 5)) {
            return MatchResult.PENDING;
        }
        else if (diff > 0) {
            return MatchResult.OWN_WIN;
        } else if (diff < 0) {
            return MatchResult.OPPONENT_WIN;
        } else {
            return MatchResult.DRAW;
        }
        //TODO: Should we carry a list for the opponents? arent there ids for the players ranking?
    }
}
