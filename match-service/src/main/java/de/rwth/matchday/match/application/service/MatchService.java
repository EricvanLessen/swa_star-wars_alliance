package de.rwth.matchday.match.application.service;

import de.rwth.matchday.match.port.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Application Service
 */
@Service
@RequiredArgsConstructor
public class MatchService {
    private final LoadIndividualMatchPort loadIndividualMatchPort;
    private final LoadMatchPort loadMatchPort;
    private final UpdateIndividualMatchPort updateIndividualMatchPort;
    private final UpdateMatchPort updateMatchPort;
    private final DeleteIndividualMatchPort deleteIndividualMatchPort;
    private final DeleteMatchPort deleteMatchPort;
    private final PlayerPort playerPort;
    //TODO Outgoing Adapter for player Service (needs a Port)
    private final PlayerClient players;


    public Match create(String opponent, LocalDate date, int teamSize) {
        if (opponent == null || opponent.isBlank() || date == null || teamSize < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "opponent, date and positive teamSize required");
        long id = ids.incrementAndGet();
        Match match = new Match(id, opponent.trim(), date, teamSize);
        matches.put(id, match);
        return match;
    }

    /**
     * get a match
     * @param id matchid of requested match
     * @return match id
     */
    public Match get(long id) {
        Match match = matches.get(id);
        if (match == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Match not found");
        return match;
    }
    //TODO domain logic should not be present in Application Service
    // Business operation: request availability synchronously; propose a deterministic lineup.
    // This is a prototype selection rule, not a table tennis league ranking policy.
    public LineupProposal propose(long matchId) {
        Match match = get(matchId);
        List<EligiblePlayer> eligible = players.eligible(matchId);
        List<EligiblePlayer> selected = eligible.stream().sorted(Comparator.comparingLong(EligiblePlayer::id)).limit(match.teamSize()).toList();
        return new LineupProposal(matchId, selected, selected.size() == match.teamSize());
    }
    //TODO should be its own class whether its a Entity or something else
    //TODO is out of scope of this service
    public record LineupProposal(long matchId, List<EligiblePlayer> players, boolean complete) {
    }
}
