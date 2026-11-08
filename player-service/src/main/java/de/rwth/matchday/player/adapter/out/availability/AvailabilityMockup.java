package de.rwth.matchday.player.adapter.out.availability;

import de.rwth.matchday.player.domain.MatchId;
import de.rwth.matchday.player.domain.PlayerId;
import de.rwth.matchday.player.port.out.AvailabilityPort;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

/** No external network required for unit/integration tests. */
@Repository
@Profile("mock-availability")
public class AvailabilityMockup implements AvailabilityPort {
    @Override public List<PlayerId> getAvailablePlayersForMatch(MatchId matchId) {
        return List.of();
    }
}

