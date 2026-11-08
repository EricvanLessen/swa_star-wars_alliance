package de.rwth.matchday.player.adapter.in.web;

import de.rwth.matchday.player.domain.*;
import de.rwth.matchday.player.port.in.CreatePlayerUseCase;
import de.rwth.matchday.player.port.in.DeletePlayerUseCase;
import de.rwth.matchday.player.port.in.PlayerEligibleForUseCase;
import de.rwth.matchday.player.port.in.UpdatePlayerUseCase;
import de.rwth.matchday.player.port.out.LoadPlayerPort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * (incoming)Rest Adapter for  Match Service
 */
@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public class PlayerController  {
    private final  UpdatePlayerUseCase updatePlayerUseCase;
    private final CreatePlayerUseCase createPlayerUseCase;
    private final DeletePlayerUseCase deletePlayerUseCase;
    private final PlayerEligibleForUseCase playerEligibleForUseCase;
    // Avoid routing simple queries through the application service.
    private final LoadPlayerPort loadPlayerPort;

    @PostMapping
    public PlayerResponse create(@RequestBody PlayerRequest request) {
        Player p = new Player(new PlayerName(request.name()), new TeamId(request.teamId()), request.active());
        createPlayerUseCase.create(p);
        return PlayerResponse.from(p);
    }

    @GetMapping
    public List<PlayerResponse> list() {
        return loadPlayerPort.loadPlayers().values().stream().map(PlayerResponse::from).toList();
    }

    @PutMapping("/{id}")
    public PlayerResponse update(@PathVariable PlayerId id, @Valid @RequestBody PlayerRequest request) {
        UpdatePlayerUseCase.UpdatePlayerCommand updatePlayerCommand = new UpdatePlayerUseCase.UpdatePlayerCommand(id, request.name(), new TeamId(request.teamId()), request.active());
        Player p = updatePlayerUseCase.update(updatePlayerCommand);
        return PlayerResponse.from(p);
    }

    @GetMapping("/{id}")
    public PlayerResponse get(@PathVariable PlayerId id) {
        return PlayerResponse.from(loadPlayerPort.loadPlayer(id));
    }

    @GetMapping("/eligible/{id}")
    public List<PlayerResponse> eligible(@PathVariable MatchId id) {
        return playerEligibleForUseCase.eligibleFor(id).stream().map(PlayerResponse::from).toList();
    }
    @DeleteMapping("/{id}")
    public PlayerResponse delete(@PathVariable PlayerId id) {
        Player p = deletePlayerUseCase.deletePlayer(id);
        return PlayerResponse.from(p);
    }
    public record PlayerRequest(
            @NotBlank String name, UUID teamId, @NotNull boolean active) {
    }

    public record PlayerResponse(UUID id, String name, UUID teamId, boolean active) {
        public static PlayerResponse from(Player p) {
            return new PlayerResponse(p.getId().playerId(), p.getName().name(),p.getTeamId().teamId(), p.isActive());
        }
    }
}
