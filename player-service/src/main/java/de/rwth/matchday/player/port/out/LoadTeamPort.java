package de.rwth.matchday.player.port.out;

import de.rwth.matchday.player.domain.Team;
import de.rwth.matchday.player.domain.TeamId;

import java.util.concurrent.ConcurrentMap;

public interface LoadTeamPort {
    ConcurrentMap<TeamId, Team> loadTeams();
    Team loadTeam(TeamId id);
}
