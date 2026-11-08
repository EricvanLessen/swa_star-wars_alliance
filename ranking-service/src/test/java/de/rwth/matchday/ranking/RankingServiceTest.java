package de.rwth.matchday.ranking;

import de.rwth.matchday.ranking.adapter.out.persistence.InMemoryResultsRepository;
import de.rwth.matchday.ranking.application.service.RankingService;
import de.rwth.matchday.ranking.domain.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RankingServiceTest {
    private static ConfirmedResult result(MatchId m, TeamId t, String season, MatchOutcome outcome) {
        return new ConfirmedResult(m, season, t, outcome);
    }

    private static RankingService service() {
        var repository = new InMemoryResultsRepository();
        return new RankingService(repository, repository);
    }

    @Test
    void calculatesProvisionalStandingsAndSeasonIsolation() {
        var service = service();
        TeamId a = new TeamId(UUID.randomUUID());
        TeamId b = new TeamId(UUID.randomUUID());
        service.record(result(new MatchId(UUID.randomUUID()), a, "2026-27", MatchOutcome.OWN_WIN));
        service.record(result(new MatchId(UUID.randomUUID()), a, "2026-27", MatchOutcome.DRAW));
        service.record(result(new MatchId(UUID.randomUUID()), b, "2026-27", MatchOutcome.DRAW));
        service.record(result(new MatchId(UUID.randomUUID()), b, "2026-27", MatchOutcome.OPPONENT_WIN));
        service.record(result(new MatchId(UUID.randomUUID()), a, "2027-28", MatchOutcome.OPPONENT_WIN));

        var table = service.standings("2026-27");
        assertEquals(2, table.size());
        assertEquals(a, table.get(0).teamId());
        assertEquals(1, table.get(0).rank());
        assertEquals(2, table.get(0).played());
        assertEquals(1, table.get(0).wins());
        assertEquals(1, table.get(0).draws());
        assertEquals(3, table.get(0).points());
        assertEquals(b, table.get(1).teamId());
        assertEquals(2, table.get(1).rank());
        assertEquals(1, table.get(1).points());

        assertEquals(1, service.standings("2027-28").size());
        assertEquals(0, service.standings("2027-28").get(0).points());
        assertTrue(service.standings("2025-26").isEmpty());
    }

    @Test
    void correctedResultIsReplacedWithoutDoubleCounting() {
        var service = service();
        TeamId team = new TeamId(UUID.randomUUID());
        MatchId match = new MatchId(UUID.randomUUID());
        var win = result(match, team, "2026-27", MatchOutcome.OWN_WIN);
        service.record(win);
        service.record(win);
        assertEquals(2, service.standings("2026-27").get(0).points());
        assertEquals(1, service.standings("2026-27").get(0).played());

        service.record(result(match, team, "2026-27", MatchOutcome.OPPONENT_WIN));
        assertEquals(0, service.standings("2026-27").get(0).points());
        assertEquals(1, service.standings("2026-27").get(0).losses());
        assertEquals(1, service.standings("2026-27").get(0).played());
    }

    @Test
    void tiesHaveSameRankWithoutInventingLeagueTieBreaks() {
        var service = service();
        service.record(result(new MatchId(UUID.randomUUID()), new TeamId(UUID.randomUUID()),
            "2026-27", MatchOutcome.DRAW));
        service.record(result(new MatchId(UUID.randomUUID()), new TeamId(UUID.randomUUID()),
            "2026-27", MatchOutcome.DRAW));
        assertEquals(1, service.standings("2026-27").get(0).rank());
        assertEquals(1, service.standings("2026-27").get(1).rank());
    }

    @Test
    void rejectsBlankSeason() {
        var service = service();
        assertThrows(IllegalArgumentException.class, () -> service.standings(" "));
    }
}

