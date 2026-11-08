package de.rwth.matchday.player.port.out;

import de.rwth.matchday.player.domain.Player;
import de.rwth.matchday.player.domain.PlayerId;

public interface UpdatePlayerPort {
    PlayerId savePlayer(Player player);
}
