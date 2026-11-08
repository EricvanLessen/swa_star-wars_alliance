package de.rwth.matchday.lineup.port.in;

import de.rwth.matchday.lineup.domain.LineupId;

public interface RejectLineupUseCase {
    void reject(LineupId id);
}

