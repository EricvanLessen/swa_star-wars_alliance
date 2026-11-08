package de.rwth.matchday.player.application.service;

import de.rwth.matchday.player.domain.*;
import de.rwth.matchday.player.port.in.*;
import de.rwth.matchday.player.port.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamService implements DeleteTeamUseCase, CreateTeamUseCase, UpdateTeamUseCase, AddTeamMemberUseCase, RemoveTeamMemberUseCase, UpdateTeamMembersUseCase {
    private final LoadTeamPort loadTeamPort;
    private final UpdateTeamPort updateTeamPort;
    private final DeleteTeamPort deleteTeamPort;
    private final LoadPlayerPort loadPlayerPort;
    private final UpdatePlayerPort updatePlayerPort;

    /**
     * create a player with given attributes
     *
     * @return the created player
     */
    public Team create(Team team) {
        updateTeamPort.saveTeam(team);
        return team;
    }

    /**
     * update Team attributes
     */
    public Team update(UpdateTeamCommand updateTeamCommand) {
        Team team = loadTeamPort.loadTeam(updateTeamCommand.id());
        //update name of Team
        if (updateTeamCommand.name() != null && !updateTeamCommand.name().isEmpty()) {
            team.changeTeamName(new TeamName(updateTeamCommand.name()));
        }
        updateTeamMembers(new UpdateTeamMembersCommand(team.getId(), updateTeamCommand.members()));

        updateTeamPort.saveTeam(team);
        return team;
    }

    @Override
    public Team deleteTeam(TeamId teamId) {
        //TODO add Team member handling
        return deleteTeamPort.deleteTeam(teamId);
    }
    /**
     * aggressive reassigning of Teammember. Even if Players are in other Teams they simply get overriden
     * @return
     */
    @Override
    public Team addTeamMember(AddTeamMemberCommand addTeamMemberCommand) {
        TeamId teamId = addTeamMemberCommand.teamId();
        PlayerId playerId = addTeamMemberCommand.playerId();

        Team team = loadTeamPort.loadTeam(teamId);
        if(team.isMember(playerId)){
            return team;
        }
        Player p = loadPlayerPort.loadPlayer(playerId);
        if(p.isInTeam()) {
            //remove current player from old Team
            Team oldTeam = loadTeamPort.loadTeam(p.getTeamId());
            oldTeam.removeMember(playerId);
            updateTeamPort.saveTeam(oldTeam);

            //add to new Team
            p.changeTeam(teamId);
            updatePlayerPort.savePlayer(p);
        }
        team.addMember(playerId);
        updateTeamPort.saveTeam(team);
        return team;
    }

    /**
     * aggressive reassigning of Teammembers. Even if Players are in other Teams they simply get overriden
     * @return
     */
    @Override
    public Team updateTeamMembers(UpdateTeamMembersCommand updateTeamMembers) {
        Team team = loadTeamPort.loadTeam(updateTeamMembers.teamId());
        if (updateTeamMembers.members() != null) {
            if (team.hasMembers()) {
                //go over old List of Members
                team.getMembers().forEach(memberId -> {
                    if (updateTeamMembers.members().contains(memberId))
                        return;
                    removeTeamMember(new RemoveTeamMemberCommand(team.getId(), memberId));
                });
            }
            updateTeamMembers.members().forEach(memberId -> {
                addTeamMember(new AddTeamMemberCommand(team.getId(), memberId));
            });
        }
        return null;
    }

    @Override
    public Team removeTeamMember(RemoveTeamMemberCommand removeTeamMemberCommand) {
        //remove from Team
        Player p = loadPlayerPort.loadPlayer(removeTeamMemberCommand.playerId());
        Team team = loadTeamPort.loadTeam(p.getTeamId());
        if(!team.isMember(p.getId())){
            throw new IllegalArgumentException("Player is not in this Team! Therefore cannot be removed");
        }
        p.changeTeam(null);
        updatePlayerPort.savePlayer(p);

        team.removeMember(p.getId());
        updateTeamPort.saveTeam(team);
        return team;
    }
}
