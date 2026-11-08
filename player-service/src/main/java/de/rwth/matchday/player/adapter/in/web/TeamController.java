package de.rwth.matchday.player.adapter.in.web;

import de.rwth.matchday.player.domain.*;
import de.rwth.matchday.player.port.in.*;
import de.rwth.matchday.player.port.out.LoadTeamPort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
/**
 * (incoming)Rest Adapter for  Team Service
 */
public class TeamController {

    private final UpdateTeamUseCase updateTeamUseCase;
    private final CreateTeamUseCase createTeamUseCase;
    private final DeleteTeamUseCase deleteTeamUseCase;
    private final AddTeamMemberUseCase addTeamMemberUseCase;
    private final UpdateTeamMembersUseCase updateTeamMembersUseCase;
    private final RemoveTeamMemberUseCase removeTeamMemberUseCase;
    // Avoid routing simple queries through the application service.
    private final LoadTeamPort loadTeamPort;

    @PostMapping
    public TeamResponse create(@RequestBody TeamRequest request) {
        Team p = new Team(new TeamName(request.name()));
        createTeamUseCase.create(p);
        return TeamResponse.from(p);
    }

    @GetMapping
    public List<TeamResponse> list() {
        return loadTeamPort.loadTeams().values().stream().map(TeamResponse::from).toList();
    }

    @PutMapping("/{id}")
    public TeamResponse update(@PathVariable TeamId id, @Valid @RequestBody TeamRequest request) {
        UpdateTeamUseCase.UpdateTeamCommand updateTeamCommand = new UpdateTeamUseCase.UpdateTeamCommand(id, request.name(),request.members());
        Team p = updateTeamUseCase.update(updateTeamCommand);
        return TeamResponse.from(p);
    }
    @PutMapping("/{teamId}/add/{memberId}")
    public TeamResponse add(@PathVariable TeamId id, @PathVariable PlayerId memberId) {
        AddTeamMemberUseCase.AddTeamMemberCommand addTeamMemberCommand = new AddTeamMemberUseCase.AddTeamMemberCommand(id, memberId);
        Team p = addTeamMemberUseCase.addTeamMember(addTeamMemberCommand);
        return TeamResponse.from(p);
    }
    @DeleteMapping("/{teamId}/remove/{memberId}")
    public TeamResponse remove(@PathVariable TeamId id, @PathVariable PlayerId memberId) {
        RemoveTeamMemberUseCase.RemoveTeamMemberCommand removeTeamMemberCommand = new RemoveTeamMemberUseCase.RemoveTeamMemberCommand(id,memberId);
        Team p = removeTeamMemberUseCase.removeTeamMember(removeTeamMemberCommand);
        return TeamResponse.from(p);
    }
    @GetMapping("/{id}")
    public TeamResponse get(@PathVariable TeamId id) {
        return TeamResponse.from(loadTeamPort.loadTeam(id));
    }
    @PutMapping("/members/{id}")
    public TeamResponse update(@PathVariable TeamId id, @Valid @RequestBody TeamMembersRequest request) {
        UpdateTeamMembersUseCase.UpdateTeamMembersCommand updateTeamMembersCommand = new UpdateTeamMembersUseCase.UpdateTeamMembersCommand(id,request.members());
        Team p = updateTeamMembersUseCase.updateTeamMembers(updateTeamMembersCommand);
        return TeamResponse.from(p);
    }
    @DeleteMapping("/{id}")
    public TeamResponse delete(@PathVariable TeamId id) {
        Team p = deleteTeamUseCase.deleteTeam(id);
        return TeamResponse.from(p);
    }

    public record TeamRequest(
            @NotBlank String name, List<PlayerId> members) {
    }
    public record TeamMembersRequest(List<PlayerId> members) {
    }

    public record TeamResponse(UUID id, String name, List<PlayerId> members) {
        public static TeamResponse from(Team team) {
            return new TeamResponse(team.getId().teamId(), team.getName().name(), team.getMembers().stream().toList());
        }
    }
}
