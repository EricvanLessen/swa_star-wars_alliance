package de.rwth.matchday.player;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/players")
public class PlayerController {
  private final PlayerService service;
  public PlayerController(PlayerService service) { this.service=service; }
  public record CreatePlayer(String name, Boolean active) {}
  public record AvailabilityRequest(Boolean available) {}
  @PostMapping public ResponseEntity<Player> create(@RequestBody CreatePlayer request) {
    Player p=service.create(request.name(),request.active()==null||request.active());
    return ResponseEntity.status(HttpStatus.CREATED).body(p);
  }
  @GetMapping public List<Player> list() { return service.all(); }
  @GetMapping("/{id}") public Player get(@PathVariable long id) { return service.get(id); }
  @PutMapping("/{id}/availability/{matchId}")
  public ResponseEntity<Void> availability(@PathVariable long id,@PathVariable long matchId,@RequestBody AvailabilityRequest request) {
    if(request.available()==null) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"available is required");
    service.setAvailability(id,matchId,request.available());
    return ResponseEntity.noContent().build();
  }
  @GetMapping("/eligible") public List<Player> eligible(@RequestParam long matchId) { return service.eligibleFor(matchId); }
}
