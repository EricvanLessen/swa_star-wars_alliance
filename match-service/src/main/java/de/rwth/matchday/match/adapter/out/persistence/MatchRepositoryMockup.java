package de.rwth.matchday.match.adapter.out.persistence;

import de.rwth.matchday.match.domain.Match;
import de.rwth.matchday.match.domain.MatchId;
import de.rwth.matchday.match.port.out.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

/** In-memory adapter for local reference use; resets on container restart. */
@Repository
public class MatchRepositoryMockup implements LoadMatchPort, UpdateMatchPort, DeleteMatchPort {
    private final ConcurrentMap<MatchId, Match> matches = new ConcurrentHashMap<>();
    @Override public ConcurrentMap<MatchId, Match> loadMatches() { return matches; }
    @Override public Match loadMatch(MatchId id) { return matches.get(id); }
    @Override public MatchId saveMatch(Match match) { matches.put(match.getId(), match); return match.getId(); }
    @Override public Match deleteMatch(MatchId id) { return matches.remove(id); }
}

