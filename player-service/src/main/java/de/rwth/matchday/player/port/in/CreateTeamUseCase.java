package de.rwth.matchday.player.port.in;

import de.rwth.matchday.player.domain.Team;

public interface CreateTeamUseCase {
    /**
     * create a team with given attributes
     * @return the created team
     */
    public Team create(Team team);
}
