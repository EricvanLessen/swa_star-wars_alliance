package de.rwth.matchday.lineup.application.service;

import de.rwth.matchday.lineup.domain.*;
import de.rwth.matchday.lineup.port.in.*;
import de.rwth.matchday.lineup.port.out.*;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

/** Application service: use-case orchestration; invariants stay in Lineup. */
@Service
public class LineupService implements ProposeLineupUseCase, ConfirmLineupUseCase,
        RejectLineupUseCase, GetLineupUseCase {

    private final LoadLineupPort load;
    private final SaveLineupPort save;

    public LineupService(LoadLineupPort load, SaveLineupPort save) {
        this.load = load;
        this.save = save;
    }

    @Override
    public LineupId propose(ProposeLineupCommand command) {
        if (command == null) throw new IllegalArgumentException("Proposal is required");
        Lineup lineup = new Lineup(command.matchId(), command.teamId(), command.playerIds());
        save.save(lineup);
        return lineup.getId();
    }

    @Override
    public void confirm(LineupId id) {
        Lineup lineup = get(id);
        lineup.confirm();
        save.save(lineup);
    }

    @Override
    public void reject(LineupId id) {
        Lineup lineup = get(id);
        lineup.reject();
        save.save(lineup);
    }

    @Override
    public Lineup get(LineupId id) {
        return load.load(id).orElseThrow(() ->
            new NoSuchElementException("Lineup not found: " + id.value()));
    }
}

