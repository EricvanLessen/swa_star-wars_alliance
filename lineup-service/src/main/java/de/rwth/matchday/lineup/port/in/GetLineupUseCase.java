package de.rwth.matchday.lineup.port.in;

import de.rwth.matchday.lineup.domain.Lineup;
import de.rwth.matchday.lineup.domain.LineupId;

public interface GetLineupUseCase {
    Lineup get(LineupId id);
}

