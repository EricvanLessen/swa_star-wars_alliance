package de.rwth.matchday.player.port.in;

import de.rwth.matchday.player.domain.Team;
import de.rwth.matchday.player.domain.TeamId;

public interface DeleteTeamUseCase {
    Team deleteTeam(TeamId teamId);
}
