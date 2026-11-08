package de.rwth.matchday.player.adapter.in.web;

import de.rwth.matchday.player.domain.*;
import de.rwth.matchday.player.port.in.*;
import de.rwth.matchday.player.port.out.LoadTeamPort;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
public class TeamController {
    private final UpdateTeamUseCase update;
    private final CreateTeamUseCase create;
    private final DeleteTeamUseCase delete;
    private final AddTeamMemberUseCase add;
    private final UpdateTeamMembersUseCase members;
    private final RemoveTeamMemberUseCase remove;
    private final LoadTeamPort loader;
    public TeamController(UpdateTeamUseCase update, CreateTeamUseCase create, DeleteTeamUseCase delete,
        AddTeamMemberUseCase add,UpdateTeamMembersUseCase members,RemoveTeamMemberUseCase remove, LoadTeamPort loader) {
        this.update=update;this.create=create;this.delete=delete;this.add=add;this.members=members;
        this.remove=remove;this.loader=loader;
    }
    public record TeamRequest(String name, List<UUID> members) {}
    public record TeamMembersRequest(List<UUID> members) {}
    public record TeamResponse(UUID id,String name,List<UUID> members) {
        static TeamResponse from(Team t) {
            if (t == null) throw new java.util.NoSuchElementException("Team not found");
            return new TeamResponse(t.getId().teamId(),t.getName().name(),
                t.getMembers().stream().map(PlayerId::playerId).toList());
        }
    }
    @PostMapping
    public TeamResponse create(@RequestBody TeamRequest request) {
        if (request == null) throw new IllegalArgumentException("Request required");
        return TeamResponse.from(create.create(new Team(new TeamName(request.name()))));
    }
    @GetMapping
    public List<TeamResponse> list(){return loader.loadTeams().values().stream().map(TeamResponse::from).toList();}
    @GetMapping("/{id}")
    public TeamResponse get(@PathVariable UUID id){return TeamResponse.from(loader.loadTeam(new TeamId(id)));}
    @PutMapping("/{id}")
    public TeamResponse update(@PathVariable UUID id,@RequestBody TeamRequest request){
        return TeamResponse.from(update.update(new UpdateTeamUseCase.UpdateTeamCommand(
            new TeamId(id),request.name(),fromIds(request.members()))));
    }
    @PutMapping("/{teamId}/add/{memberId}")
    public TeamResponse add(@PathVariable UUID teamId,@PathVariable UUID memberId){
        return TeamResponse.from(add.addTeamMember(
            new AddTeamMemberUseCase.AddTeamMemberCommand(new TeamId(teamId),new PlayerId(memberId))));
    }
    @DeleteMapping("/{teamId}/remove/{memberId}")
    public TeamResponse remove(@PathVariable UUID teamId,@PathVariable UUID memberId){
        return TeamResponse.from(remove.removeTeamMember(
            new RemoveTeamMemberUseCase.RemoveTeamMemberCommand(new TeamId(teamId),new PlayerId(memberId))));
    }
    @PutMapping("/members/{id}")
    public TeamResponse members(@PathVariable UUID id,@RequestBody TeamMembersRequest request){
        return TeamResponse.from(members.updateTeamMembers(
            new UpdateTeamMembersUseCase.UpdateTeamMembersCommand(new TeamId(id),fromIds(request.members()))));
    }
    @DeleteMapping("/{id}")
    public TeamResponse delete(@PathVariable UUID id){return TeamResponse.from(delete.deleteTeam(new TeamId(id)));}
    private static List<PlayerId> fromIds(List<UUID> ids){
        return ids==null?null:ids.stream().map(PlayerId::new).toList();
    }
}

