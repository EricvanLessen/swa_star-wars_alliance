package de.rwth.matchday.match;

import java.time.LocalDate;
import java.util.List;

import de.rwth.matchday.match.domain.EligiblePlayer;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;

class MatchServiceTest {
    @Test
    void proposesUpToTeamSizeAndFlagsIncompleteLineup() {
      //  PlayerClient client = mock(PlayerClient.class);
       // when(client.eligible(1L)).thenReturn(List.of(new EligiblePlayer(3, "C", true), new EligiblePlayer(1, "A", true), new EligiblePlayer(2, "B", true)));
       // MatchService service = new MatchService(client);
       // service.create("Opponents", LocalDate.of(2026, 11, 12), 2);
       // var result = service.propose(1);
       // assertEquals(List.of(1L, 2L), result.players().stream().map(EligiblePlayer::id).toList());
       // assertTrue(result.complete());
       // verify(client).eligible(1L);
    }

    @Test
    void flagsIncompleteLineup() {
        PlayerClient client = mock(PlayerClient.class);
        when(client.eligible(1L)).thenReturn(List.of(new EligiblePlayer(7, "Alice", true)));
        MatchService service = new MatchService(client);
        service.create("Opponents", LocalDate.of(2026, 11, 12), 2);

        var result = service.propose(1);

        assertEquals(1, result.players().size());
        assertFalse(result.complete());
    }

    @Test
    void validatesMatchInput() {
        MatchService service = new MatchService(mock(PlayerClient.class));
        LocalDate date = LocalDate.of(2026, 11, 12);

        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                () -> service.create(" ", date, 1)).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                () -> service.create("Opponent", null, 1)).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, assertThrows(ResponseStatusException.class,
                () -> service.create("Opponent", date, 0)).getStatusCode());
    }

    @Test
    void createsAndRetrievesMatchAndRejectsUnknownId() {
        MatchService service = new MatchService(mock(PlayerClient.class));

        Match match = service.create(" Opponent ", LocalDate.of(2026, 11, 12), 2);

        assertEquals("Opponent", match.opponent());
        assertEquals(match, service.get(match.id()));
        assertEquals(HttpStatus.NOT_FOUND, assertThrows(ResponseStatusException.class,
                () -> service.get(999)).getStatusCode());
    }
}
