package de.rwth.matchday.lineup;

import de.rwth.matchday.lineup.domain.*;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LineupDomainTest {
    private final MatchId matchId = new MatchId(UUID.randomUUID());
    private final TeamId teamId = new TeamId(UUID.randomUUID());
    private final PlayerId alice = new PlayerId(UUID.randomUUID());

    @Test
    void followsProposedConfirmedLifecycle() {
        Lineup lineup = new Lineup(matchId, teamId, List.of(alice));
        assertEquals(LineupStatus.PROPOSED, lineup.getStatus());
        lineup.confirm();
        assertEquals(LineupStatus.CONFIRMED, lineup.getStatus());
        assertThrows(IllegalStateException.class, lineup::confirm);
        assertThrows(IllegalStateException.class, lineup::reject);
    }

    @Test
    void rejectedLineupCannotBeConfirmed() {
        Lineup lineup = new Lineup(matchId, teamId, List.of(alice));
        lineup.reject();
        assertEquals(LineupStatus.REJECTED, lineup.getStatus());
        assertThrows(IllegalStateException.class, lineup::confirm);
    }

    @Test
    void rejectsMissingAndDuplicatedPlayers() {
        assertThrows(IllegalArgumentException.class, () -> new Lineup(matchId, teamId, List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new Lineup(matchId, teamId, List.of(alice, alice)));
        assertThrows(IllegalArgumentException.class, () -> new Lineup(matchId, teamId, null));
    }

    @Test
    void playerListCannotBeModifiedOutsideAggregate() {
        Lineup lineup = new Lineup(matchId, teamId, List.of(alice));
        assertThrows(UnsupportedOperationException.class, () -> lineup.getPlayerIds().clear());
    }
}

