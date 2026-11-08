package de.rwth.matchday.match.port.out;

import de.rwth.matchday.match.domain.Match;
import de.rwth.matchday.match.domain.MatchId;

public interface DeleteMatchPort {
    public Match deleteMatch(MatchId id);
}
