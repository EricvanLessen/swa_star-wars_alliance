package de.rwth.matchday.player;

import de.rwth.matchday.player.application.service.PlayerService;
import de.rwth.matchday.player.domain.*;
import de.rwth.matchday.player.port.in.UpdatePlayerUseCase.UpdatePlayerCommand;
import de.rwth.matchday.player.port.out.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerServiceTest {
    private final LoadPlayerPort players = mock(LoadPlayerPort.class);
    private final UpdatePlayerPort updates = mock(UpdatePlayerPort.class);
    private final AvailabilityPort availability = mock(AvailabilityPort.class);
    private final DeletePlayerPort deletions = mock(DeletePlayerPort.class);
    private final LoadTeamPort teams = mock(LoadTeamPort.class);
    private final UpdateTeamPort teamUpdates = mock(UpdateTeamPort.class);
    private final PlayerService service = new PlayerService(players, updates, availability, deletions, teams, teamUpdates);

    @Test
    void createsAndPersistsPlayer() {
        Player player = new Player(new PlayerName("Alice"), true);

        assertSame(player, service.create(player));
        verify(updates).savePlayer(player);
    }

    @Test
    void eligibilityRequiresActivePlayerAndAvailability() {
        Player alice = new Player(new PlayerName("Alice"), true);
        Player bob = new Player(new PlayerName("Bob"), false);
        Player carol = new Player(new PlayerName("Carol"), true);
        MatchId matchId = new MatchId();
        var stored = new ConcurrentHashMap<PlayerId, Player>();
        for (Player player : List.of(alice, bob, carol)) {
            stored.put(player.getId(), player);
        }
        when(players.loadPlayers()).thenReturn(stored);
        when(availability.getAvailablePlayersForMatch(matchId)).thenReturn(List.of(alice.getId(), bob.getId()));

        assertEquals(List.of(alice), service.eligibleFor(matchId));
    }

    @Test
    void updatesPlayerAndPersistsChanges() {
        Player player = new Player(new PlayerName("Alice"), true);
        TeamId teamId = new TeamId();
        when(players.loadPlayer(player.getId())).thenReturn(player);

        Player updated = service.update(new UpdatePlayerCommand(player.getId(), "Alicia", teamId, false));

        assertEquals("Alicia", updated.getName().name());
        assertEquals(teamId, updated.getTeamId());
        assertFalse(updated.isActive());
        verify(updates).savePlayer(player);
    }

    @Test
    void deletingTeamMemberAlsoRemovesThemFromTeam() {
        Team team = new Team(new TeamName("Example"));
        Player player = new Player(new PlayerName("Alice"), team.getId(), true);
        team.addMember(player.getId());
        when(players.loadPlayer(player.getId())).thenReturn(player);
        when(teams.loadTeam(team.getId())).thenReturn(team);
        when(deletions.deletePlayer(player.getId())).thenReturn(player);

        assertSame(player, service.deletePlayer(player.getId()));
        assertFalse(team.isMember(player.getId()));
        verify(teamUpdates).saveTeam(team);
        verify(deletions).deletePlayer(player.getId());
    }

    @Test
    void playerNameIsTrimmedAndMustHaveValidLength() {
        assertEquals("Alice", new PlayerName(" Alice ").name());
        assertThrows(IllegalArgumentException.class, () -> new PlayerName("Al"));
        assertThrows(IllegalArgumentException.class, () -> new PlayerName("A".repeat(21)));
    }
}
