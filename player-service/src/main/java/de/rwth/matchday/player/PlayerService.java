package de.rwth.matchday.player;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
@Service
public class PlayerService {
  private final AtomicLong ids = new AtomicLong();
  private final ConcurrentMap<Long,Player> players = new ConcurrentHashMap<>();
  private final ConcurrentMap<Long,ConcurrentMap<Long,Boolean>> availability = new ConcurrentHashMap<>();
  public Player create(String name, boolean active) {
    if (name == null || name.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Player name is required");
    long id=ids.incrementAndGet();
    Player player=new Player(id,name.trim(),active);
    players.put(id,player); return player;
  }
  public List<Player> all() { return players.values().stream().sorted(Comparator.comparingLong(Player::id)).toList(); }
  public Player get(long id) { Player p=players.get(id); if(p==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Player not found"); return p; }
  public void setAvailability(long playerId,long matchId,boolean available) {
    if (matchId<=0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid match ID");
    get(playerId);
    availability.computeIfAbsent(matchId,k->new ConcurrentHashMap<>()).put(playerId,available);
  }
  // Business operation: only active players who explicitly opted in are eligible.
  public List<Player> eligibleFor(long matchId) {
    if(matchId<=0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid match ID");
    Map<Long,Boolean> responses=availability.getOrDefault(matchId,new ConcurrentHashMap<>());
    return players.values().stream().filter(Player::active).filter(p->Boolean.TRUE.equals(responses.get(p.id())))
      .sorted(Comparator.comparingLong(Player::id)).toList();
  }
}
