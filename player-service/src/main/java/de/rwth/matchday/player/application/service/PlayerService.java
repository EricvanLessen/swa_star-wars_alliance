package de.rwth.matchday.player.application.service;

import de.rwth.matchday.player.domain.*;
import de.rwth.matchday.player.port.in.*;
import de.rwth.matchday.player.port.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Application Service - Application Layer
 * this class orchestrate the service
 */
@Service
@RequiredArgsConstructor
public class PlayerService implements CreatePlayerUseCase,PlayerEligibleForUseCase, UpdatePlayerUseCase, DeletePlayerUseCase  {

    private final LoadPlayerPort loadPlayerPort;
    private final UpdatePlayerPort updatePlayerPort;
    private final AvailabilityPort availabilityPort;
    private final DeletePlayerPort deletePlayerPort;
    private final LoadTeamPort loadTeamPort;
    private final UpdateTeamPort updateTeamPort;
    /**
     * create a player with given attributes
     * @return the created player
     */
    public Player create(Player player) {
        updatePlayerPort.savePlayer(player);
        return player;
    }
    public Player update(UpdatePlayerCommand updatePlayerCommand) {
        Player p = loadPlayerPort.loadPlayer(updatePlayerCommand.id());
        if (updatePlayerCommand.name() != null && !updatePlayerCommand.name().isEmpty()) {
            p.changeName(new PlayerName(updatePlayerCommand.name()));
        }
        if(updatePlayerCommand.teamId().isTeam()){
            p.changeTeam(updatePlayerCommand.teamId());
        }
        if(updatePlayerCommand.active() != p.isActive()){
            p.toggleActive();
        }
        updatePlayerPort.savePlayer(p);
        return p;
    }
    /**
     * get all members eligible for match with given matchId
     * @param matchId the matchId of the match that all eligible members are requested of
     * @return list of eligible members - empty if match does not exist
     */
    // TODO this function belongs to the domain service - Business operation: only active members who explicitly opted in are eligible.
    public List<Player> eligibleFor(MatchId matchId) {
        //if (matchId <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid match ID");
        List<PlayerId> responses = availabilityPort.getAvailablePlayersForMatch(matchId);
        //TODO add that this list is filtered by Teams...
        return loadPlayerPort.loadPlayers().values().stream().filter(Player::isActive).filter(p -> responses.contains(p.getId())).toList();
    }

    @Override
    public Player deletePlayer(PlayerId id) {
        //TODO add TeamId handling
        Player p = loadPlayerPort.loadPlayer(id);
        if(p.isInTeam()){
            Team team = loadTeamPort.loadTeam(p.getTeamId());
            team.removeMember(p.getId());
            updateTeamPort.saveTeam(team);
        }
        return deletePlayerPort.deletePlayer(id);
    }
}
