package de.rwth.matchday.llm.port.in;

import de.rwth.matchday.llm.domain.MatchReport;
import de.rwth.matchday.llm.domain.MatchReportInput;

public interface GenerateMatchReportUseCase {
    MatchReport generate(MatchReportInput input);
}

