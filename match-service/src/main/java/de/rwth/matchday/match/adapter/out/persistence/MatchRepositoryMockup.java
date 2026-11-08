package de.rwth.matchday.match.adapter.out.persistence;

import de.rwth.matchday.match.domain.Match;
import de.rwth.matchday.match.domain.MatchId;
import de.rwth.matchday.match.port.out.DeleteMatchPort;
import de.rwth.matchday.match.port.out.LoadMatchPort;
import de.rwth.matchday.match.port.out.UpdateMatchPort;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MatchRepositoryMockup implements LoadMatchPort, UpdateMatchPort, DeleteMatchPort {

    private final ConcurrentMap<MatchId, Match> matches = new ConcurrentHashMap<>();

    @Override
    public ConcurrentMap<MatchId, Match> loadMatches() {
        return matches;
    }

    @Override
    public Match loadMatch(MatchId id) {
        return matches.get(id);
    }

    @Override
    public MatchId saveMatch(Match Match) {
        matches.put(Match.getId(), Match);
        return Match.getId();
    }

    @Override
    public Match deleteMatch(MatchId id) {
        return matches.remove(id);
    }
}
