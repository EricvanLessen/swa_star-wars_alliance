package de.rwth.matchday.match;

import de.rwth.matchday.match.application.service.MatchService;
import de.rwth.matchday.match.adapter.out.persistence.MatchRepositoryMockup;
import de.rwth.matchday.match.domain.*;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatchServiceTest {
    private static MatchService service() {
        var r = new MatchRepositoryMockup();
        return new MatchService(r, r);
    }
    @Test void createsRecordsAndConfirms() {
        var s = service();
        var m = s.create(UUID.randomUUID(), "Alemannia", "Opponents",
            LocalDate.of(2026,11,12), "2026-27", Venue.HOME);
        assertEquals(MatchResult.PENDING, m.result());
        assertThrows(IllegalStateException.class, () -> s.confirmed(m.getId().matchId()));
        s.record(m.getId().matchId(),6,4);
        assertEquals(MatchResult.OWN_WIN, m.result());
        s.confirm(m.getId().matchId());
        assertTrue(s.confirmed(m.getId().matchId()).isConfirmed());
        s.record(m.getId().matchId(),3,5);
        assertFalse(m.isConfirmed());
        assertEquals(MatchResult.OPPONENT_WIN, m.result());
    }
    @Test void validatesInputAndUnknownIds() {
        var s=service();
        assertThrows(IllegalArgumentException.class,
            ()->s.create(UUID.randomUUID()," ","Opponent",LocalDate.now(),"2026-27",Venue.HOME));
        assertThrows(NoSuchElementException.class,()->s.get(UUID.randomUUID()));
    }
}

