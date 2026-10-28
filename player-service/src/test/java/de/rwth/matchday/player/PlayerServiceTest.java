package de.rwth.matchday.player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class PlayerServiceTest {
 @Test void eligibleRequiresActiveAndExplicitAvailability() {
  PlayerService service=new PlayerService();
  Player alice=service.create("Alice",true);
  Player bob=service.create("Bob",false);
  Player carol=service.create("Carol",true);
  service.setAvailability(alice.id(),42,true);
  service.setAvailability(bob.id(),42,true);
  service.setAvailability(carol.id(),42,false);
  assertEquals(java.util.List.of(alice),service.eligibleFor(42));
  assertTrue(service.eligibleFor(43).isEmpty());
 }
}
