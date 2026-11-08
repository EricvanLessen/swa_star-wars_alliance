package de.rwth.matchday.player.port.out;

import de.rwth.matchday.player.domain.Player;
import de.rwth.matchday.player.domain.PlayerId;

import java.util.concurrent.ConcurrentMap;

public interface LoadPlayerPort {
    ConcurrentMap<PlayerId, Player> loadPlayers();
    Player loadPlayer(PlayerId id);
}
