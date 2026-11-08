package de.rwth.matchday.player.port.in;

import de.rwth.matchday.player.domain.PlayerId;
import de.rwth.matchday.player.domain.Team;
import de.rwth.matchday.player.domain.TeamId;

public interface AddTeamMemberUseCase {
    public record AddTeamMemberCommand(TeamId teamId, PlayerId playerId){}
    public Team addTeamMember(AddTeamMemberCommand addTeamMemberCommand);
}
