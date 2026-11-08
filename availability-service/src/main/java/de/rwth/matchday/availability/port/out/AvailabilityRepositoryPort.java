package de.rwth.matchday.availability.port.out;
import de.rwth.matchday.availability.domain.PlayerAvailability;
import java.util.List;
import java.util.UUID;
public interface AvailabilityRepositoryPort {
  void save(PlayerAvailability availability);
  List<UUID> availablePlayers(UUID matchId);
}

