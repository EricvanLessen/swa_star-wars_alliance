package de.rwth.matchday.ranking;

import de.rwth.matchday.ranking.domain.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RankingDomainTest {
    private final MatchId match = new MatchId(UUID.randomUUID());
    private final TeamId team = new TeamId(UUID.randomUUID());

    @Test
    void onlyConfirmedCompletedResultsCanEnterRanking() {
        assertThrows(IllegalArgumentException.class,
            () -> new ConfirmedResult(match, "2026-27", team, MatchOutcome.PENDING));
        assertThrows(IllegalArgumentException.class,
            () -> new ConfirmedResult(match, " ", team, MatchOutcome.OWN_WIN));
        assertThrows(IllegalArgumentException.class,
            () -> new ConfirmedResult(match, "2026-27", team, null));
        assertThrows(IllegalArgumentException.class,
            () -> new ConfirmedResult(match, "2026-27", null, MatchOutcome.DRAW));
        assertEquals("2026-27",
            new ConfirmedResult(match, " 2026-27 ", team, MatchOutcome.DRAW).season());
    }
}

