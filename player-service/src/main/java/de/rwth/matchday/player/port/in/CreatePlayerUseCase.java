package de.rwth.matchday.player.port.in;

import de.rwth.matchday.player.domain.Player;

public interface CreatePlayerUseCase {
    /**
     * create a player with given attributes
     * @return the created player
     */
    public Player create(Player player);
}
