package de.rwth.matchday.ranking.port.out;

import de.rwth.matchday.ranking.domain.ConfirmedResult;
import java.util.List;

public interface LoadResultsPort {
    List<ConfirmedResult> resultsForSeason(String season);
}

