package de.rwth.matchday.player.domain;

import lombok.Getter;

/**
 * Aggregate/Entity representing a player
 */
@Getter
public class Player {
    private final PlayerId id;
    private PlayerName name;
    private TeamId teamId;
    private boolean active;

    public Player(PlayerId id, PlayerName name, TeamId teamId, boolean active) {
        this.id = id;
        this.name = name;
        this.teamId = teamId;
        this.active = active;
    }
    //TODO an Aggregate or Entity should have Logic beyond getter and setters
    //TODO primitive types are to be avoided
    public Player(PlayerName name, TeamId teamId, boolean active) {
        this(new PlayerId(), name, teamId, active);
    }
    public Player(PlayerName name, boolean active) {
        this(new PlayerId(), name, null, active);
    }
    public void changeName(PlayerName newName) {
        this.name = newName;
    }
    public void changeTeam(TeamId newTeamId) {
        this.teamId = newTeamId;
    }
    public boolean isInTeam() {
        if(teamId == null) return true;
        return this.teamId.teamId() != null;
    }
    public void toggleActive() {
        this.active = !this.active;
    }
}
