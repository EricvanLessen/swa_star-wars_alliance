package de.rwth.matchday.match;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
@Service
public class MatchService {
 private final AtomicLong ids=new AtomicLong();
 private final ConcurrentMap<Long,Match> matches=new ConcurrentHashMap<>();
 private final PlayerClient players;
 public MatchService(PlayerClient players){this.players=players;}
 public Match create(String opponent,LocalDate date,int teamSize){
  if(opponent==null||opponent.isBlank()||date==null||teamSize<1)
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"opponent, date and positive teamSize required");
  long id=ids.incrementAndGet();
  Match match=new Match(id,opponent.trim(),date,teamSize);
  matches.put(id,match);return match;
 }
 public Match get(long id){
  Match match=matches.get(id);
  if(match==null)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Match not found");
  return match;
 }
 // Business operation: request availability synchronously; propose a deterministic lineup.
 // This is a prototype selection rule, not a table tennis league ranking policy.
 public LineupProposal propose(long matchId){
  Match match=get(matchId);
  List<EligiblePlayer> eligible=players.eligible(matchId);
  List<EligiblePlayer> selected=eligible.stream().sorted(Comparator.comparingLong(EligiblePlayer::id)).limit(match.teamSize()).toList();
  return new LineupProposal(matchId,selected,selected.size()==match.teamSize());
 }
 public record LineupProposal(long matchId,List<EligiblePlayer> players,boolean complete){}
}
