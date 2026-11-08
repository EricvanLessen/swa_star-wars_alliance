package de.rwth.matchday.ranking.adapter.in.web;

import de.rwth.matchday.ranking.domain.*;
import de.rwth.matchday.ranking.port.in.*;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

/**
 * REST contract for accepted, confirmed result snapshots and club-team standings.
 * Only a trusted caller should send results after the result-confirmation workflow.
 */
@RestController
@RequestMapping("/rankings")
public class RankingController {
    private final RecordConfirmedResultUseCase record;
    private final GetStandingsUseCase getStandings;

    public RankingController(RecordConfirmedResultUseCase record, GetStandingsUseCase getStandings) {
        this.record = record;
        this.getStandings = getStandings;
    }

    public record ResultRequest(String season, UUID teamId, MatchOutcome result) {}
    public record ResultResponse(UUID matchId, String season, UUID teamId, MatchOutcome result) {
        static ResultResponse from(ConfirmedResult r) {
            return new ResultResponse(r.matchId().value(), r.season(), r.teamId().value(), r.outcome());
        }
    }
    public record StandingResponse(UUID teamId, int played, int wins, int draws,
                                   int losses, int points, int rank) {
        static StandingResponse from(Standing standing) {
            return new StandingResponse(standing.teamId().value(), standing.played(),
                standing.wins(), standing.draws(), standing.losses(), standing.points(), standing.rank());
        }
    }
    public record RankingResponse(String season, String scoringRule, List<StandingResponse> standings) {}

    @PutMapping("/results/{matchId}")
    public ResultResponse record(@PathVariable UUID matchId, @RequestBody ResultRequest request) {
        if (request == null) throw new IllegalArgumentException("Request body is required");
        ConfirmedResult result = new ConfirmedResult(
            new MatchId(matchId), request.season(), new TeamId(request.teamId()), request.result());
        return ResultResponse.from(record.record(result));
    }

    @GetMapping
    public RankingResponse get(@RequestParam String season) {
        return new RankingResponse(season.trim(), "DEMO_WIN_2_DRAW_1_LOSS_0",
            getStandings.standings(season).stream().map(StandingResponse::from).toList());
    }
}

