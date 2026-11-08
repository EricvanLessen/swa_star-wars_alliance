package de.rwth.matchday.player.port.in;

import de.rwth.matchday.player.domain.PlayerId;
import de.rwth.matchday.player.domain.Team;
import de.rwth.matchday.player.domain.TeamId;

public interface RemoveTeamMemberUseCase {
    public record RemoveTeamMemberCommand(TeamId id, PlayerId playerId){}
    Team removeTeamMember(RemoveTeamMemberCommand command);
}
