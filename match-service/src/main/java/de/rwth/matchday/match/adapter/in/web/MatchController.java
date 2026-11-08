package de.rwth.matchday.match.adapter.in.web;

import de.rwth.matchday.match.application.service.MatchService;
import de.rwth.matchday.match.domain.Match;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * (Incoming) Rest Adapter for Frontend and player Service
 */
@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
//TODO break up the Adpater into seperate classes for each use case
public class MatchController {
    private final MatchService service;

    /**
     * create a match
     * @param request create match command message
     * @return some sort of wrapped response of a match
     */
    @PostMapping
    public ResponseEntity<Match> create(@RequestBody CreateMatch request) {
        Match match = service.create(request.opponent(), request.date(), request.teamSize() == null ? 0 : request.teamSize());
        return ResponseEntity.status(HttpStatus.CREATED).body(match);
    }

    /**
     * get a match
     * @param id matchId of the match requested
     * @return match if id exist null else
     */
    @GetMapping("/{id}")
    public Match get(@PathVariable long id) {
        return service.get(id);
    }

    /**
     * get a Lineup proposal for a match
     * @param id playerId of the player for the lineup
     * @return Lineup
     */
    //TODO this is not scope of this Service... this belongs to Lineup Service
    @PostMapping("/{id}/lineup/propose")
    public MatchService.LineupProposal propose(@PathVariable long id) {
        return service.propose(id);
    }

    //create Match Message
    public record CreateMatch(String opponent, LocalDate date, Integer teamSize) {
    }
}
