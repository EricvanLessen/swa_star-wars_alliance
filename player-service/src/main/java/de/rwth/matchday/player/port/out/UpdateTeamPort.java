package de.rwth.matchday.player.port.out;

import de.rwth.matchday.player.domain.Player;
import de.rwth.matchday.player.domain.PlayerId;
import de.rwth.matchday.player.domain.Team;
import de.rwth.matchday.player.domain.TeamId;

public interface UpdateTeamPort {
    TeamId saveTeam(Team team);
}
