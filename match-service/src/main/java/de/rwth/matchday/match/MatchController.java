package de.rwth.matchday.match;
import java.time.LocalDate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/matches")
public class MatchController {
 private final MatchService service;
 public MatchController(MatchService service){this.service=service;}
 public record CreateMatch(String opponent,LocalDate date,Integer teamSize){}
 @PostMapping public ResponseEntity<Match> create(@RequestBody CreateMatch request){
  Match match=service.create(request.opponent(),request.date(),request.teamSize()==null?0:request.teamSize());
  return ResponseEntity.status(HttpStatus.CREATED).body(match);
 }
 @GetMapping("/{id}") public Match get(@PathVariable long id){return service.get(id);}
 @PostMapping("/{id}/lineup/propose") public MatchService.LineupProposal propose(@PathVariable long id){return service.propose(id);}
}
