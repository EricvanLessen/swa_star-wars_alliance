package de.rwth.matchday.ranking.port.in;

import de.rwth.matchday.ranking.domain.ConfirmedResult;

public interface RecordConfirmedResultUseCase {
    ConfirmedResult record(ConfirmedResult result);
}

