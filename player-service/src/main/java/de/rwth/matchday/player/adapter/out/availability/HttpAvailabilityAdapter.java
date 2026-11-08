package de.rwth.matchday.player.adapter.out.availability;

import de.rwth.matchday.player.domain.MatchId;
import de.rwth.matchday.player.domain.PlayerId;
import de.rwth.matchday.player.port.out.AvailabilityPort;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

/** Outgoing REST adapter: Player consumes Availability, never stores those responses. */
@Component
@Profile("!mock-availability")
public class HttpAvailabilityAdapter implements AvailabilityPort {
    public record Response(UUID matchId, List<UUID> playerIds) {}
    private final RestClient client;
    public HttpAvailabilityAdapter(@Value("${availability.service.url}") String baseUrl) {
        client=RestClient.builder().baseUrl(baseUrl).build();
    }
    @Override public List<PlayerId> getAvailablePlayersForMatch(MatchId matchId) {
        try {
            Response response=client.get()
                .uri("/availabilities/matches/{matchId}",matchId.matchId())
                .retrieve().body(Response.class);
            return response==null || response.playerIds()==null ? List.of()
                : response.playerIds().stream().map(PlayerId::new).toList();
        } catch(RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,"Availability Service unavailable",e);
        }
    }
}

