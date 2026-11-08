package de.rwth.matchday.lineup.adapter.out.persistence;

import de.rwth.matchday.lineup.domain.*;
import de.rwth.matchday.lineup.port.out.*;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

/** Replaceable infrastructure adapter; no database required for the current exercise. */
@Repository
public class InMemoryLineupRepository implements LoadLineupPort, SaveLineupPort {
    private final ConcurrentMap<LineupId, Lineup> lineups = new ConcurrentHashMap<>();

    @Override
    public Optional<Lineup> load(LineupId id) {
        return Optional.ofNullable(lineups.get(id));
    }

    @Override
    public void save(Lineup lineup) {
        lineups.put(lineup.getId(), lineup);
    }
}

