package de.rwth.matchday.player.port.in;

import de.rwth.matchday.player.domain.Player;
import de.rwth.matchday.player.domain.PlayerId;
import de.rwth.matchday.player.domain.TeamId;

public interface UpdatePlayerUseCase {
    // Application: Command
    public record UpdatePlayerCommand(PlayerId id, String name, TeamId teamId, boolean active) {
    }

    public Player update(UpdatePlayerCommand updatePlayerCommand);
}
