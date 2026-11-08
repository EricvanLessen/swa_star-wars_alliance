package de.rwth.matchday.availability.domain;
import java.util.UUID;
public record PlayerAvailability(UUID matchId, UUID playerId, boolean available) {
    public PlayerAvailability {
      if (matchId == null || playerId == null) throw new IllegalArgumentException("matchId and playerId required");
    }
}

