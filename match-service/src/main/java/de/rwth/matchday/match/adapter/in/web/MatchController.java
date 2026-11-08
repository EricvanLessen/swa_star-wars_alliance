package de.rwth.matchday.match.adapter.in.web;

import de.rwth.matchday.match.application.service.MatchService;
import de.rwth.matchday.match.domain.*;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Match scheduling, result entry and confirmation. */
@RestController
@RequestMapping("/matches")
public class MatchController {
    private final MatchService service;

    public MatchController(MatchService service) { this.service = service; }

    public record CreateMatch(UUID teamId, String teamName, String opponent,
                              LocalDate date, String season, Venue venue) {}
    public record ResultRequest(int ownGames, int opponentGames) {}
    public record MatchResponse(UUID id, UUID teamId, String teamName,
        String opponent, LocalDate date, String season, Venue venue,
        Integer ownGames, Integer opponentGames, boolean confirmed, MatchResult result) {
        static MatchResponse from(Match m) {
            return new MatchResponse(m.getId().matchId(), m.getTeamId().teamId(), m.getTeamName(),
                m.getOpponentTeamName(), m.getDate(), m.getSeason(), m.getVenue(),
                m.getOwnGames(), m.getOpponentGames(), m.isConfirmed(), m.result());
        }
    }
    public record ConfirmedResultResponse(UUID matchId, UUID teamId, String season, MatchResult result) {}
    public record ReportData(UUID matchId, String team, String opponent, LocalDate date,
                             int ownGames, int opponentGames, List<String> highlights) {}

    @PostMapping
    public ResponseEntity<MatchResponse> create(@RequestBody CreateMatch request) {
        if (request == null) throw new IllegalArgumentException("Request required");
        Match match = service.create(request.teamId(), request.teamName(),
            request.opponent(), request.date(), request.season(), request.venue());
        return ResponseEntity.created(URI.create("/matches/" + match.getId()))
            .body(MatchResponse.from(match));
    }
    @GetMapping("/{id}")
    public MatchResponse get(@PathVariable UUID id) {
        return MatchResponse.from(service.get(id));
    }
    @PutMapping("/{id}/result")
    public MatchResponse record(@PathVariable UUID id, @RequestBody ResultRequest result) {
        if (result == null) throw new IllegalArgumentException("Result required");
        return MatchResponse.from(service.record(id, result.ownGames(), result.opponentGames()));
    }
    @PostMapping("/{id}/confirm")
    public MatchResponse confirm(@PathVariable UUID id) {
        return MatchResponse.from(service.confirm(id));
    }
    @GetMapping("/{id}/confirmed-result")
    public ConfirmedResultResponse confirmed(@PathVariable UUID id) {
        Match match = service.confirmed(id);
        return new ConfirmedResultResponse(match.getId().matchId(),
            match.getTeamId().teamId(), match.getSeason(), match.result());
    }
    @GetMapping("/{id}/report-data")
    public ReportData reportData(@PathVariable UUID id) {
        Match m = service.confirmed(id);
        return new ReportData(m.getId().matchId(), m.getTeamName(), m.getOpponentTeamName(),
            m.getDate(), m.getOwnGames(), m.getOpponentGames(), List.of());
    }
}

