package de.rwth.matchday.ranking.port.in;

import de.rwth.matchday.ranking.domain.Standing;
import java.util.List;

public interface GetStandingsUseCase {
    List<Standing> standings(String season);
}

