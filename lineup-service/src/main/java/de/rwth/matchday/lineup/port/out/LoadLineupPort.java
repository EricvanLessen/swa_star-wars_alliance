package de.rwth.matchday.lineup.port.out;

import de.rwth.matchday.lineup.domain.Lineup;
import de.rwth.matchday.lineup.domain.LineupId;
import java.util.Optional;

public interface LoadLineupPort {
    Optional<Lineup> load(LineupId id);
}

