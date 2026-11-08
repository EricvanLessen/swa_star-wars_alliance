package de.rwth.matchday.lineup.adapter.in.web;

import de.rwth.matchday.lineup.domain.*;
import de.rwth.matchday.lineup.port.in.*;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Incoming REST adapter: maps HTTP DTOs to technology-independent use cases. */
@RestController
@RequestMapping("/lineups")
public class LineupController {
    private final ProposeLineupUseCase propose;
    private final ConfirmLineupUseCase confirm;
    private final RejectLineupUseCase reject;
    private final GetLineupUseCase get;

    public LineupController(ProposeLineupUseCase propose, ConfirmLineupUseCase confirm,
                            RejectLineupUseCase reject, GetLineupUseCase get) {
        this.propose = propose;
        this.confirm = confirm;
        this.reject = reject;
        this.get = get;
    }

    public record ProposalRequest(UUID matchId, UUID teamId, List<UUID> playerIds) {}
    public record CreatedLineup(UUID id) {}
    public record LineupResponse(UUID id, UUID matchId, UUID teamId,
                                 List<UUID> playerIds, LineupStatus status) {
        static LineupResponse from(Lineup lineup) {
            return new LineupResponse(lineup.getId().value(), lineup.getMatchId().value(),
                lineup.getTeamId().value(), lineup.getPlayerIds().stream().map(PlayerId::value).toList(),
                lineup.getStatus());
        }
    }

    @PostMapping
    public ResponseEntity<CreatedLineup> propose(@RequestBody ProposalRequest request) {
        if (request.playerIds() == null)
            throw new IllegalArgumentException("playerIds are required");
        var command = new ProposeLineupUseCase.ProposeLineupCommand(
            new MatchId(request.matchId()),
            new TeamId(request.teamId()),
            request.playerIds().stream().map(PlayerId::new).toList());
        LineupId id = propose.propose(command);
        return ResponseEntity.created(URI.create("/lineups/" + id.value()))
            .body(new CreatedLineup(id.value()));
    }

    @GetMapping("/{id}")
    public LineupResponse get(@PathVariable UUID id) {
        return LineupResponse.from(get.get(new LineupId(id)));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable UUID id) {
        confirm.confirm(new LineupId(id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable UUID id) {
        reject.reject(new LineupId(id));
        return ResponseEntity.noContent().build();
    }
}

