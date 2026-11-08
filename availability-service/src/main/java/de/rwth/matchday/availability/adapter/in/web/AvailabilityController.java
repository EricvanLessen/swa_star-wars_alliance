package de.rwth.matchday.availability.adapter.in.web;
import de.rwth.matchday.availability.domain.PlayerAvailability;
import de.rwth.matchday.availability.port.in.AvailabilityUseCase;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/availabilities/matches")
public class AvailabilityController {
  private final AvailabilityUseCase service;
  public AvailabilityController(AvailabilityUseCase service) { this.service=service; }
  public record AvailabilityRequest(boolean available) {}
  public record AvailablePlayersResponse(UUID matchId, List<UUID> playerIds) {}
  @PutMapping("/{matchId}/players/{playerId}")
  public PlayerAvailability declare(@PathVariable UUID matchId, @PathVariable UUID playerId,
                                    @RequestBody AvailabilityRequest request) {
    if (request == null) throw new IllegalArgumentException("Request body required");
    return service.declare(matchId,playerId,request.available());
  }
  @GetMapping("/{matchId}")
  public AvailablePlayersResponse available(@PathVariable UUID matchId) {
    return new AvailablePlayersResponse(matchId,service.availablePlayers(matchId));
  }
}

