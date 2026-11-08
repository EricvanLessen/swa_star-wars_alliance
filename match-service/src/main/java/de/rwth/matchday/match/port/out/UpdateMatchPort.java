package de.rwth.matchday.match.port.out;

import de.rwth.matchday.match.domain.Match;
import de.rwth.matchday.match.domain.MatchId;

public interface UpdateMatchPort {
    public MatchId saveMatch(Match Match);
}
