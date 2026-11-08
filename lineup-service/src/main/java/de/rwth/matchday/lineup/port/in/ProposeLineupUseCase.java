package de.rwth.matchday.lineup.port.in;

import de.rwth.matchday.lineup.domain.*;
import java.util.List;

public interface ProposeLineupUseCase {
    LineupId propose(ProposeLineupCommand command);

    record ProposeLineupCommand(MatchId matchId, TeamId teamId, List<PlayerId> playerIds) {}
}

