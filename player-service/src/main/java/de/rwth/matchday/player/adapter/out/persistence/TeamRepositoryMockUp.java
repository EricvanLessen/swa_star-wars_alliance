package de.rwth.matchday.player.adapter.out.persistence;

import de.rwth.matchday.player.domain.Team;
import de.rwth.matchday.player.domain.TeamId;
import de.rwth.matchday.player.port.out.DeleteTeamPort;
import de.rwth.matchday.player.port.out.LoadTeamPort;
import de.rwth.matchday.player.port.out.UpdateTeamPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
@Profile("mockup")
@RequiredArgsConstructor
public class TeamRepositoryMockUp implements LoadTeamPort, UpdateTeamPort, DeleteTeamPort {
    private final ConcurrentMap<TeamId, Team> teams = new ConcurrentHashMap<>();


    @Override
    public Team deleteTeam(TeamId id) {
        return teams.remove(id);
    }

    @Override
    public ConcurrentMap<TeamId, Team> loadTeams() {
        return teams;
    }

    @Override
    public Team loadTeam(TeamId id) {
        return teams.get(id);
    }

    @Override
    public TeamId saveTeam(Team team) {
        teams.put(team.getId(), team);
        return team.getId();
    }
}
