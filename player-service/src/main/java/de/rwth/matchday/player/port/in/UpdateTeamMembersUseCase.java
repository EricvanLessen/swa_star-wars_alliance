package de.rwth.matchday.player.port.in;

import de.rwth.matchday.player.domain.Player;
import de.rwth.matchday.player.domain.PlayerId;
import de.rwth.matchday.player.domain.Team;
import de.rwth.matchday.player.domain.TeamId;

import java.util.List;

public interface UpdateTeamMembersUseCase {

    public record UpdateTeamMembersCommand(TeamId teamId, List<PlayerId> members){}
    public Team updateTeamMembers(UpdateTeamMembersCommand updateTeamMembers);
}
