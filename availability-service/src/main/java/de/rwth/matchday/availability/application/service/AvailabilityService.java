package de.rwth.matchday.availability.application.service;
import de.rwth.matchday.availability.domain.PlayerAvailability;
import de.rwth.matchday.availability.port.in.AvailabilityUseCase;
import de.rwth.matchday.availability.port.out.AvailabilityRepositoryPort;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
@Service
public class AvailabilityService implements AvailabilityUseCase {
  private final AvailabilityRepositoryPort repository;
  public AvailabilityService(AvailabilityRepositoryPort repository) { this.repository=repository; }
  @Override public PlayerAvailability declare(UUID matchId, UUID playerId, boolean available) {
    var declaration=new PlayerAvailability(matchId, playerId, available);
    repository.save(declaration);
    return declaration;
  }
  @Override public List<UUID> availablePlayers(UUID matchId) {
    if (matchId == null) throw new IllegalArgumentException("matchId required");
    return repository.availablePlayers(matchId);
  }
}

