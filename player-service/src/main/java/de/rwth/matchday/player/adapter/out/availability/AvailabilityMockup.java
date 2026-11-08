package de.rwth.matchday.player.adapter.out.availability;

import de.rwth.matchday.player.domain.MatchId;
import de.rwth.matchday.player.domain.PlayerId;
import de.rwth.matchday.player.port.out.AvailabilityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Mockup for avail
 */
@Repository
@Profile("mockup")
@RequiredArgsConstructor
public class AvailabilityMockup implements AvailabilityPort {
    private final ConcurrentMap<MatchId, List<PlayerId>> availability = new ConcurrentHashMap<>();

    @Override
    public List<PlayerId> getAvailablePlayersForMatch(MatchId matchId) {
        return availability.getOrDefault(matchId, List.of());
    }
}
