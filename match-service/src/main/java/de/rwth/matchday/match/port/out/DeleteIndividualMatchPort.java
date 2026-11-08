package de.rwth.matchday.match.port.out;

import de.rwth.matchday.match.domain.IndividualMatch;
import de.rwth.matchday.match.domain.IndividualMatchId;


public interface DeleteIndividualMatchPort {
    public IndividualMatch deleteIndividualMatch(IndividualMatchId id);
}
