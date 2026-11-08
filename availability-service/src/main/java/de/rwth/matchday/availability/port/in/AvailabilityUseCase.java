package de.rwth.matchday.availability.port.in;
import de.rwth.matchday.availability.domain.PlayerAvailability;
import java.util.List;
import java.util.UUID;
public interface AvailabilityUseCase {
  PlayerAvailability declare(UUID matchId, UUID playerId, boolean available);
  List<UUID> availablePlayers(UUID matchId);
}

