package de.rwth.matchday.player.port.out;

import de.rwth.matchday.player.domain.Team;
import de.rwth.matchday.player.domain.TeamId;

public interface DeleteTeamPort {
    Team deleteTeam(TeamId id);
}
