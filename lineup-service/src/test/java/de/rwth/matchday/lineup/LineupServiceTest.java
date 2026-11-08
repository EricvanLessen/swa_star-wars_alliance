package de.rwth.matchday.lineup;

import de.rwth.matchday.lineup.adapter.out.persistence.InMemoryLineupRepository;
import de.rwth.matchday.lineup.application.service.LineupService;
import de.rwth.matchday.lineup.domain.*;
import de.rwth.matchday.lineup.port.in.ProposeLineupUseCase.ProposeLineupCommand;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LineupServiceTest {
    @Test
    void proposesLoadsAndConfirmsLineup() {
        var repository = new InMemoryLineupRepository();
        var service = new LineupService(repository, repository);
        LineupId id = service.propose(new ProposeLineupCommand(
            new MatchId(UUID.randomUUID()),
            new TeamId(UUID.randomUUID()),
            List.of(new PlayerId(UUID.randomUUID()))));

        assertEquals(LineupStatus.PROPOSED, service.get(id).getStatus());
        service.confirm(id);
        assertEquals(LineupStatus.CONFIRMED, service.get(id).getStatus());
    }

    @Test
    void unknownLineupCannotBeReviewed() {
        var repository = new InMemoryLineupRepository();
        var service = new LineupService(repository, repository);
        assertThrows(NoSuchElementException.class, () -> service.confirm(new LineupId()));
        assertThrows(NoSuchElementException.class, () -> service.reject(new LineupId()));
    }
}

