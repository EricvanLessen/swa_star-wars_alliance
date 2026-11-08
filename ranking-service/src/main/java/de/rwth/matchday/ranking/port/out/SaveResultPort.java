package de.rwth.matchday.ranking.port.out;

import de.rwth.matchday.ranking.domain.ConfirmedResult;

public interface SaveResultPort {
    /** Upsert by match ID; a corrected result replaces the prior snapshot. */
    void save(ConfirmedResult result);
}

