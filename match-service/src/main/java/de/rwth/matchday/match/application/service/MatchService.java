package de.rwth.matchday.match.application.service;

import de.rwth.matchday.match.domain.*;
import de.rwth.matchday.match.port.out.*;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;

/** Owns match workflows; other contexts consume a confirmed result snapshot. */
@Service
public class MatchService {
    private final LoadMatchPort loader;
    private final UpdateMatchPort writer;

    public MatchService(LoadMatchPort loader, UpdateMatchPort writer) {
        this.loader = loader;
        this.writer = writer;
    }

    public Match create(UUID teamId, String teamName, String opponent, LocalDate date,
                        String season, Venue venue) {
        Match match = new Match(new MatchId(), date, opponent,
            new TeamId(teamId), venue == null ? Venue.HOME : venue, teamName, season);
        writer.saveMatch(match);
        return match;
    }
    public Match get(UUID id) {
        Match match = loader.loadMatch(new MatchId(id));
        if (match == null) throw new NoSuchElementException("Match not found");
        return match;
    }
    public Match record(UUID id, int ownGames, int opponentGames) {
        Match match = get(id);
        match.recordResult(ownGames, opponentGames);
        writer.saveMatch(match);
        return match;
    }
    public Match confirm(UUID id) {
        Match match = get(id);
        match.confirmResult();
        writer.saveMatch(match);
        return match;
    }
    public Match confirmed(UUID id) {
        Match match = get(id);
        if (!match.isConfirmed()) throw new IllegalStateException("Match result is not confirmed");
        return match;
    }
}

