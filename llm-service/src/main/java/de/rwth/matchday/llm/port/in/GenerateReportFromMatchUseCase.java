package de.rwth.matchday.llm.port.in;
import de.rwth.matchday.llm.domain.MatchReport;
import java.util.UUID;
public interface GenerateReportFromMatchUseCase {
    MatchReport generateFor(UUID matchId);
}

