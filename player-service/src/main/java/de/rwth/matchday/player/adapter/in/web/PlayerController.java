package de.rwth.matchday.player.adapter.in.web;

import de.rwth.matchday.player.domain.*;
import de.rwth.matchday.player.port.in.*;
import de.rwth.matchday.player.port.out.LoadPlayerPort;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

/** REST boundary for player CRUD and availability-based eligibility. */
@RestController
@RequestMapping("/players")
public class PlayerController {
    private final UpdatePlayerUseCase update;
    private final CreatePlayerUseCase create;
    private final DeletePlayerUseCase delete;
    private final PlayerEligibleForUseCase eligible;
    private final LoadPlayerPort loader;

    public PlayerController(UpdatePlayerUseCase update, CreatePlayerUseCase create,
                            DeletePlayerUseCase delete, PlayerEligibleForUseCase eligible,
                            LoadPlayerPort loader) {
        this.update=update; this.create=create; this.delete=delete; this.eligible=eligible; this.loader=loader;
    }

    public record PlayerRequest(String name, UUID teamId, boolean active) {}
    public record PlayerResponse(UUID id, String name, UUID teamId, boolean active) {
        static PlayerResponse from(Player p) {
            if (p == null) throw new java.util.NoSuchElementException("Player not found");
            return new PlayerResponse(p.getId().playerId(),p.getName().name(),
                p.isInTeam()?p.getTeamId().teamId():null,p.isActive());
        }
    }
    @PostMapping
    public PlayerResponse create(@RequestBody PlayerRequest request) {
        if (request == null) throw new IllegalArgumentException("Request required");
        var p = new Player(new PlayerName(request.name()),
            request.teamId()==null?null:new TeamId(request.teamId()),request.active());
        return PlayerResponse.from(create.create(p));
    }
    @GetMapping
    public List<PlayerResponse> list() { return loader.loadPlayers().values().stream().map(PlayerResponse::from).toList(); }
    @PutMapping("/{id}")
    public PlayerResponse update(@PathVariable UUID id,@RequestBody PlayerRequest request) {
        if (request == null) throw new IllegalArgumentException("Request required");
        return PlayerResponse.from(update.update(new UpdatePlayerUseCase.UpdatePlayerCommand(
            new PlayerId(id),request.name(),request.teamId()==null?null:new TeamId(request.teamId()),request.active())));
    }
    @GetMapping("/eligible/{id}")
    public List<PlayerResponse> eligible(@PathVariable UUID id) {
        return eligible.eligibleFor(new MatchId(id)).stream().map(PlayerResponse::from).toList();
    }
    @GetMapping("/{id}")
    public PlayerResponse get(@PathVariable UUID id) { return PlayerResponse.from(loader.loadPlayer(new PlayerId(id))); }
    @DeleteMapping("/{id}")
    public PlayerResponse delete(@PathVariable UUID id) { return PlayerResponse.from(delete.deletePlayer(new PlayerId(id))); }
}

