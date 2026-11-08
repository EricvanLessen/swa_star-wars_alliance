package de.rwth.matchday.player.port.in;

import de.rwth.matchday.player.domain.PlayerId;
import de.rwth.matchday.player.domain.Team;
import de.rwth.matchday.player.domain.TeamId;

import java.util.List;

public interface UpdateTeamUseCase {
    // Application: Command
    public record UpdateTeamCommand(TeamId id, String name, List<PlayerId> members) {
    }

    public Team update(UpdateTeamCommand updateTeamCommand);
}
