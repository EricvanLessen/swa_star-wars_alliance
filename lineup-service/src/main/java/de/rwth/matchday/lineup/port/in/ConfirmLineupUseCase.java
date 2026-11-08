package de.rwth.matchday.lineup.port.in;

import de.rwth.matchday.lineup.domain.LineupId;

public interface ConfirmLineupUseCase {
    void confirm(LineupId id);
}

