package de.rwth.matchday.match.port.out;

import de.rwth.matchday.match.domain.Match;
import de.rwth.matchday.match.domain.MatchId;

import java.util.concurrent.ConcurrentMap;

public interface LoadMatchPort {
    public Match loadMatch(MatchId id);
    public ConcurrentMap<MatchId, Match> loadMatches();
}
