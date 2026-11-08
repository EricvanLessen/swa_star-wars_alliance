package de.rwth.matchday.match.port.out;

import de.rwth.matchday.match.domain.IndividualMatch;
import de.rwth.matchday.match.domain.IndividualMatchId;
import de.rwth.matchday.match.domain.Match;
import de.rwth.matchday.match.domain.MatchId;

public interface UpdateIndividualMatchPort {
    public IndividualMatchId saveIndividualMatch(IndividualMatch individualMatch);
}
