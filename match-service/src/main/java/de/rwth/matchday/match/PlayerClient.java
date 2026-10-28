package de.rwth.matchday.match;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import org.springframework.web.server.ResponseStatusException;
@Component
public class PlayerClient {
 private final RestClient client;
 public PlayerClient(@Value("${player.service.url}") String url) { client=RestClient.builder().baseUrl(url).build(); }
 public List<EligiblePlayer> eligible(long matchId) {
  try {
   EligiblePlayer[] response=client.get().uri(uri->uri.path("/players/eligible").queryParam("matchId",matchId).build())
       .retrieve().body(EligiblePlayer[].class);
   return response==null?List.of():Arrays.asList(response);
  } catch(RestClientException e) {
   throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Player Service unavailable",e);
  }
 }
}
