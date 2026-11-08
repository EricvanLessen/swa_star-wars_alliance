package de.rwth.matchday.availability.adapter.out.persistence;
import de.rwth.matchday.availability.domain.PlayerAvailability;
import de.rwth.matchday.availability.port.out.AvailabilityRepositoryPort;
import java.util.*;
import java.util.concurrent.*;
import org.springframework.stereotype.Repository;
@Repository
public class InMemoryAvailabilityRepository implements AvailabilityRepositoryPort {
  private final ConcurrentMap<UUID, ConcurrentMap<UUID,Boolean>> matches = new ConcurrentHashMap<>();
  @Override public void save(PlayerAvailability a) {
    matches.computeIfAbsent(a.matchId(), ignored -> new ConcurrentHashMap<>())
      .put(a.playerId(), a.available());
  }
  @Override public List<UUID> availablePlayers(UUID matchId) {
    return matches.getOrDefault(matchId, new ConcurrentHashMap<>()).entrySet().stream()
      .filter(Map.Entry::getValue).map(Map.Entry::getKey)
      .sorted(Comparator.comparing(UUID::toString)).toList();
  }
}

