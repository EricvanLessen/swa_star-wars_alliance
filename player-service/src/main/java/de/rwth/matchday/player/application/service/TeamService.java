package de.rwth.matchday.player.application.service;

import de.rwth.matchday.player.domain.*;
import de.rwth.matchday.player.port.in.*;
import de.rwth.matchday.player.port.out.*;
import java.util.HashSet;
import org.springframework.stereotype.Service;

/** Membership updates keep Player.teamId and Team.members consistent. */
@Service
public class TeamService implements DeleteTeamUseCase,CreateTeamUseCase,UpdateTeamUseCase,
        AddTeamMemberUseCase,RemoveTeamMemberUseCase,UpdateTeamMembersUseCase {
    private final LoadTeamPort teams;
    private final UpdateTeamPort saveTeam;
    private final DeleteTeamPort deleteTeam;
    private final LoadPlayerPort players;
    private final UpdatePlayerPort savePlayer;
    public TeamService(LoadTeamPort teams,UpdateTeamPort saveTeam,DeleteTeamPort deleteTeam,
                       LoadPlayerPort players,UpdatePlayerPort savePlayer) {
        this.teams=teams;this.saveTeam=saveTeam;this.deleteTeam=deleteTeam;
        this.players=players;this.savePlayer=savePlayer;
    }
    @Override public Team create(Team team) { saveTeam.saveTeam(team); return team; }
    @Override public Team update(UpdateTeamCommand cmd) {
        Team team=requiredTeam(cmd.id());
        if (cmd.name()!=null) team.changeTeamName(new TeamName(cmd.name()));
        if (cmd.members()!=null) updateTeamMembers(new UpdateTeamMembersCommand(cmd.id(),cmd.members()));
        saveTeam.saveTeam(team);
        return team;
    }
    @Override public Team deleteTeam(TeamId id) {
        Team team=requiredTeam(id);
        for (PlayerId playerId: new HashSet<>(team.getMembers())) {
            removeTeamMember(new RemoveTeamMemberCommand(id,playerId));
        }
        return deleteTeam.deleteTeam(id);
    }
    @Override public Team addTeamMember(AddTeamMemberCommand cmd) {
        Team team=requiredTeam(cmd.teamId());
        Player player=requiredPlayer(cmd.playerId());
        if (player.isInTeam() && !player.getTeamId().equals(team.getId())) {
            Team oldTeam=requiredTeam(player.getTeamId());
            oldTeam.removeMember(player.getId());
            saveTeam.saveTeam(oldTeam);
        }
        player.changeTeam(team.getId());
        team.addMember(player.getId());
        savePlayer.savePlayer(player);
        saveTeam.saveTeam(team);
        return team;
    }
    @Override public Team removeTeamMember(RemoveTeamMemberCommand cmd) {
        Team team=requiredTeam(cmd.teamId());
        Player player=requiredPlayer(cmd.playerId());
        if (!team.isMember(player.getId()) || !cmd.teamId().equals(player.getTeamId()))
            throw new IllegalArgumentException("Player does not belong to this team");
        team.removeMember(player.getId());
        player.changeTeam(null);
        savePlayer.savePlayer(player);
        saveTeam.saveTeam(team);
        return team;
    }
    @Override public Team updateTeamMembers(UpdateTeamMembersCommand cmd) {
        Team team=requiredTeam(cmd.teamId());
        if (cmd.members()==null) return team;
        var desired=new HashSet<>(cmd.members());
        for (PlayerId playerId: new HashSet<>(team.getMembers()))
            if (!desired.contains(playerId)) removeTeamMember(
                new RemoveTeamMemberCommand(team.getId(),playerId));
        for (PlayerId playerId: desired) addTeamMember(new AddTeamMemberCommand(team.getId(),playerId));
        return team;
    }
    private Team requiredTeam(TeamId id) {
        Team t=teams.loadTeam(id);
        if (t==null) throw new java.util.NoSuchElementException("Team not found");
        return t;
    }
    private Player requiredPlayer(PlayerId id) {
        Player p=players.loadPlayer(id);
        if (p==null) throw new java.util.NoSuchElementException("Player not found");
        return p;
    }
}

