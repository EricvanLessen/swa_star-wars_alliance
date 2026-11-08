package de.rwth.matchday.player.adapter.out.persistence;

import de.rwth.matchday.player.domain.Player;
import de.rwth.matchday.player.domain.PlayerId;
import de.rwth.matchday.player.port.out.DeletePlayerPort;
import de.rwth.matchday.player.port.out.LoadPlayerPort;
import de.rwth.matchday.player.port.out.UpdatePlayerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
@Profile("mockup")
@RequiredArgsConstructor
public class PlayerRepositoryMockup implements LoadPlayerPort, UpdatePlayerPort, DeletePlayerPort {
    private final ConcurrentMap<PlayerId, Player> players = new ConcurrentHashMap<>();

    @Override
    public ConcurrentMap<PlayerId, Player> loadPlayers() {
        return players;
    }

    @Override
    public Player loadPlayer(PlayerId id) {
        return players.get(id);
    }

    @Override
    public PlayerId savePlayer(Player player) {
        players.put(player.getId(), player);
        return player.getId();
    }

    @Override
    public Player deletePlayer(PlayerId id) {
        return players.remove(id);
    }
}
