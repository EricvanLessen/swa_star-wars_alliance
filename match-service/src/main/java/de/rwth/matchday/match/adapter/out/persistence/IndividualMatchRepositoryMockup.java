package de.rwth.matchday.match.adapter.out.persistence;

import de.rwth.matchday.match.domain.IndividualMatch;
import de.rwth.matchday.match.domain.IndividualMatchId;
import de.rwth.matchday.match.domain.Match;
import de.rwth.matchday.match.domain.MatchId;
import de.rwth.matchday.match.port.out.DeleteIndividualMatchPort;
import de.rwth.matchday.match.port.out.LoadIndividualMatchPort;
import de.rwth.matchday.match.port.out.UpdateIndividualMatchPort;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IndividualMatchRepositoryMockup implements LoadIndividualMatchPort, UpdateIndividualMatchPort, DeleteIndividualMatchPort {
    
    private final ConcurrentMap<IndividualMatchId, IndividualMatch> IndividualMatches = new ConcurrentHashMap<>();

    @Override
    public ConcurrentMap<IndividualMatchId, IndividualMatch> loadIndividualMatches() {
        return IndividualMatches;
    }

    @Override
    public List<IndividualMatch> loadIndividualMatches(List<IndividualMatchId> ids) {
        return IndividualMatches.keySet().stream().map(IndividualMatches::get).filter(Objects::nonNull).toList();
    }

    @Override
    public IndividualMatch loadIndividualMatch(IndividualMatchId id) {
        return IndividualMatches.get(id);
    }

    @Override
    public IndividualMatchId saveIndividualMatch(IndividualMatch IndividualMatch) {
        IndividualMatches.put(IndividualMatch.getId(), IndividualMatch);
        return IndividualMatch.getId();
    }

    @Override
    public IndividualMatch deleteIndividualMatch(IndividualMatchId id) {
        return IndividualMatches.remove(id);
    }

}
