package de.rwth.matchday.llm.port.out;
import de.rwth.matchday.llm.domain.MatchReportInput;
import java.util.UUID;
public interface LoadMatchReportDataPort {
    MatchReportInput load(UUID matchId);
}

