package de.rwth.matchday.match.port.out;

import de.rwth.matchday.match.domain.IndividualMatch;
import de.rwth.matchday.match.domain.IndividualMatchId;
import de.rwth.matchday.match.domain.MatchId;

import java.security.PublicKey;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public interface LoadIndividualMatchPort {
    public IndividualMatch loadIndividualMatch(IndividualMatchId id);
    public ConcurrentMap<IndividualMatchId, IndividualMatch> loadIndividualMatches();
    public List<IndividualMatch> loadIndividualMatches(List<IndividualMatchId> ids);
}
