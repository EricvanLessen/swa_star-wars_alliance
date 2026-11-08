package de.rwth.matchday.ranking.domain;

import java.util.*;

/** Pure domain service: provisional points policy and deterministic standings. */
public final class RankingCalculator {
    public List<Standing> calculate(List<ConfirmedResult> results) {
        Map<TeamId, Totals> totals = new HashMap<>();
        for (ConfirmedResult result : results) {
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

    private static final class Totals {
        int wins;
        int draws;
        int losses;
        int points() { return wins * 2 + draws; }
    }
}
