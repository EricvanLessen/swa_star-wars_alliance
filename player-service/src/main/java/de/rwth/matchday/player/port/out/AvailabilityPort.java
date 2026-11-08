package de.rwth.matchday.player.port.out;

import de.rwth.matchday.player.domain.MatchId;
import de.rwth.matchday.player.domain.PlayerId;

import java.util.List;
import java.util.UUID;

public interface AvailabilityPort {
    List<PlayerId> getAvailablePlayersForMatch(MatchId matchId);
}
