package de.rwth.matchday.match.adapter.out.player;

import de.rwth.matchday.match.domain.EligiblePlayer;
import de.rwth.matchday.match.port.out.PlayerPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

/**
 * (outgoing) Rest Adapter for player Service
 */
@Component
public class PlayerClient implements PlayerPort {
    private final RestClient client;

    /**
     * default constructor
     * @param url url of player service (defined in application properties)
     */
    public PlayerClient(@Value("${player.service.url}") String url) {
        client = RestClient.builder().baseUrl(url).build();
    }

    /**
     * request eligble players for a match
     * @param matchId matchid of match requested
     * @return List of Eligible Players
     */
    public List<EligiblePlayer> eligible(long matchId) {
        try {
            EligiblePlayer[] response = client.get().uri(uri -> uri.path("/players/eligible").queryParam("matchId", matchId).build())
                    .retrieve().body(EligiblePlayer[].class);
            return response == null ? List.of() : Arrays.asList(response);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Player Service unavailable", e);
        }
    }
}
