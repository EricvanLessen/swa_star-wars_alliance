package de.rwth.matchday.player.port.out;

import de.rwth.matchday.player.domain.Player;
import de.rwth.matchday.player.domain.PlayerId;

public interface DeletePlayerPort {
    Player deletePlayer(PlayerId id);
}
