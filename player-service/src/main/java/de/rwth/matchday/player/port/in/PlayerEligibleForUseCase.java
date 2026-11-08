package de.rwth.matchday.player.port.in;

import de.rwth.matchday.player.domain.MatchId;
import de.rwth.matchday.player.domain.Player;

import java.util.List;

public interface PlayerEligibleForUseCase {
    public List<Player> eligibleFor(MatchId matchId);
}
