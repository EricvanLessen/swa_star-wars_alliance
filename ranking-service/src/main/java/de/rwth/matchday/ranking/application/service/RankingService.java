package de.rwth.matchday.ranking.application.service;

import de.rwth.matchday.ranking.domain.*;
import de.rwth.matchday.ranking.port.in.*;
import de.rwth.matchday.ranking.port.out.*;
import java.util.*;
import org.springframework.stereotype.Service;

/** Application service orchestrating the Ranking bounded context. */
@Service
public class RankingService implements RecordConfirmedResultUseCase, GetStandingsUseCase {
    private final LoadResultsPort loadResults;
    private final SaveResultPort saveResult;

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

        Map<TeamId, Totals> totals = new HashMap<>();
        for (ConfirmedResult result : loadResults.resultsForSeason(season.trim())) {
            Totals t = totals.computeIfAbsent(result.teamId(), ignored -> new Totals());
            switch (result.outcome()) {
                case OWN_WIN -> t.wins++;
                case OPPONENT_WIN -> t.losses++;
                case DRAW -> t.draws++;
                case PENDING -> throw new IllegalStateException("Pending result cannot be ranked");
            }
        }

        List<Map.Entry<TeamId, Totals>> sorted = new ArrayList<>(totals.entrySet());
        sorted.sort(Comparator.<Map.Entry<TeamId, Totals>>comparingInt(e -> e.getValue().points())
                .reversed().thenComparing(e -> e.getKey().value().toString()));

        List<Standing> ranking = new ArrayList<>();
        int previousPoints = Integer.MIN_VALUE;
        int rank = 0;
        for (int i = 0; i < sorted.size(); i++) {
            var entry = sorted.get(i);
            Totals t = entry.getValue();
            if (t.points() != previousPoints) rank = i + 1;
            ranking.add(new Standing(entry.getKey(), t.wins + t.draws + t.losses,
                    t.wins, t.draws, t.losses, t.points(), rank));
            previousPoints = t.points();
        }
        return List.copyOf(ranking);
    }

    private static class Totals {
        int wins;
        int draws;
        int losses;
        int points() { return wins * 2 + draws; }
    }
}

