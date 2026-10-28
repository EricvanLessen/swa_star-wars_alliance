package de.rwth.matchday.match;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class MatchServiceTest {
 @Test void proposesUpToTeamSizeAndFlagsIncompleteLineup(){
  PlayerClient client=mock(PlayerClient.class);
  when(client.eligible(1L)).thenReturn(List.of(new EligiblePlayer(3,"C",true),new EligiblePlayer(1,"A",true),new EligiblePlayer(2,"B",true)));
  MatchService service=new MatchService(client);
  service.create("Opponents",LocalDate.of(2026,11,12),2);
  var result=service.propose(1);
  assertEquals(List.of(1L,2L),result.players().stream().map(EligiblePlayer::id).toList());
  assertTrue(result.complete());
  verify(client).eligible(1L);
 }
}
