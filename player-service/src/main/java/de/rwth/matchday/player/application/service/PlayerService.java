package de.rwth.matchday.player.application.service;

import de.rwth.matchday.player.domain.*;
import de.rwth.matchday.player.port.in.*;
import de.rwth.matchday.player.port.out.*;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;

@Service
public class PlayerService implements CreatePlayerUseCase,PlayerEligibleForUseCase,
 UpdatePlayerUseCase,DeletePlayerUseCase {
 private final LoadPlayerPort players;
 private final UpdatePlayerPort savePlayer;
 private final AvailabilityPort availability;
 private final DeletePlayerPort deletePlayer;
 private final LoadTeamPort teams;
 private final UpdateTeamPort saveTeam;
 public PlayerService(LoadPlayerPort players, UpdatePlayerPort savePlayer,
     AvailabilityPort availability,DeletePlayerPort deletePlayer,
     LoadTeamPort teams,UpdateTeamPort saveTeam) {
    this.players=players;this.savePlayer=savePlayer;this.availability=availability;
    this.deletePlayer=deletePlayer;this.teams=teams;this.saveTeam=saveTeam;
 }
 @Override public Player create(Player p) {
    savePlayer.savePlayer(p);
    if (p.isInTeam()) {
        Team team=teams.loadTeam(p.getTeamId());
        if (team==null) throw new NoSuchElementException("Team not found");
        team.addMember(p.getId());saveTeam.saveTeam(team);
    }
    return p;
 }
 @Override public Player update(UpdatePlayerCommand cmd) {
    Player p=requiredPlayer(cmd.id());
    if (cmd.name()!=null) p.changeName(new PlayerName(cmd.name()));
    if (cmd.teamId()!=null && cmd.teamId().isTeam() &&
        !cmd.teamId().equals(p.getTeamId())) {
        if (p.isInTeam()) {
            Team old=teams.loadTeam(p.getTeamId());
            if (old!=null) {old.removeMember(p.getId());saveTeam.saveTeam(old);}
        }
        Team team=teams.loadTeam(cmd.teamId());
        if (team==null) throw new NoSuchElementException("Team not found");
        team.addMember(p.getId());saveTeam.saveTeam(team);
        p.changeTeam(cmd.teamId());
    }
    if (cmd.active()!=p.isActive()) p.toggleActive();
    savePlayer.savePlayer(p);
    return p;
 }
 @Override public List<Player> eligibleFor(MatchId matchId) {
    List<PlayerId> available=availability.getAvailablePlayersForMatch(matchId);
    return players.loadPlayers().values().stream().filter(Player::isActive)
      .filter(p->available.contains(p.getId())).toList();
 }
 @Override public Player deletePlayer(PlayerId id) {
    Player p=requiredPlayer(id);
    if (p.isInTeam()) {
        Team team=teams.loadTeam(p.getTeamId());
        if (team!=null) {team.removeMember(p.getId());saveTeam.saveTeam(team);}
    }
    return deletePlayer.deletePlayer(id);
 }
 private Player requiredPlayer(PlayerId id) {
    Player p=players.loadPlayer(id);
    if (p==null) throw new NoSuchElementException("Player not found");
    return p;
 }
}

