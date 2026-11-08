package de.rwth.matchday.llm.adapter.in.web;

import de.rwth.matchday.llm.domain.MatchReport;
import de.rwth.matchday.llm.domain.MatchReportInput;
import de.rwth.matchday.llm.port.in.GenerateMatchReportUseCase;
import de.rwth.matchday.llm.port.in.GenerateReportFromMatchUseCase;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/llm")
public class MatchReportController {
    private final GenerateMatchReportUseCase generate;
    private final GenerateReportFromMatchUseCase fromMatch;
    public MatchReportController(GenerateMatchReportUseCase generate,GenerateReportFromMatchUseCase fromMatch) {
        this.generate=generate;this.fromMatch=fromMatch;
    }
    @PostMapping("/match-reports")
    public MatchReport generate(@RequestBody MatchReportInput request) {
        return generate.generate(request);
    }
    @PostMapping("/match-reports/from-match/{matchId}")
    public MatchReport fromMatch(@PathVariable UUID matchId) {
        return fromMatch.generateFor(matchId);
    }
}

