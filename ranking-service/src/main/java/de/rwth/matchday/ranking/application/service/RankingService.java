package de.rwth.matchday.ranking.application.service;

import de.rwth.matchday.ranking.domain.*;
import de.rwth.matchday.ranking.port.in.*;
import de.rwth.matchday.ranking.port.out.*;
import java.util.List;
import org.springframework.stereotype.Service;

/** Application service: orchestration, with ranking policy in the domain. */
@Service
public class RankingService implements RecordConfirmedResultUseCase, GetStandingsUseCase {
    private final LoadResultsPort loadResults;
    private final SaveResultPort saveResult;
    private final RankingCalculator calculator = new RankingCalculator();

    public RankingService(LoadResultsPort loadResults, SaveResultPort saveResult) {
        this.loadResults = loadResults;
        this.saveResult = saveResult;
    }

    @Override
    public ConfirmedResult record(ConfirmedResult result) {
        if (result == null) throw new IllegalArgumentException("Result is required");
        saveResult.save(result);
        return result;
    }

    @Override
    public List<Standing> standings(String season) {
        if (season == null || season.isBlank())
            throw new IllegalArgumentException("season is required");
        return calculator.calculate(loadResults.resultsForSeason(season.trim()));
    }
}
