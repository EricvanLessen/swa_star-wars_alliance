package de.rwth.matchday.ranking.adapter.out.persistence;

import de.rwth.matchday.ranking.domain.*;
import de.rwth.matchday.ranking.port.out.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryResultsRepository implements LoadResultsPort, SaveResultPort {
    private final ConcurrentMap<MatchId, ConfirmedResult> results = new ConcurrentHashMap<>();

    @Override
    public void save(ConfirmedResult result) {
        results.put(result.matchId(), result);
    }

    @Override
    public List<ConfirmedResult> resultsForSeason(String season) {
        return results.values().stream()
            .filter(r -> r.season().equals(season))
            .toList();
    }
}

