package de.rwth.matchday.player.port.in;

import de.rwth.matchday.player.domain.Player;
import de.rwth.matchday.player.domain.PlayerId;

public interface DeletePlayerUseCase {
    Player deletePlayer(PlayerId id);
}
