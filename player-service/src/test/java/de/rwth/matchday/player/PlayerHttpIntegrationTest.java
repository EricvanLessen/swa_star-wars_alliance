package de.rwth.matchday.player;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlayerHttpIntegrationTest {
    private static final Pattern ID = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");
    private final HttpClient client = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    @Test
    void createsAndRetrievesPlayerOverHttp() throws Exception {
        var team = request("POST", "/teams", "{\"name\":\"Example\"}");
        assertEquals(200, team.statusCode());
        String teamId = idFrom(team.body());

        var created = request("POST", "/players",
                "{\"name\":\"Alice\",\"teamId\":\"" + teamId + "\",\"active\":true}");
        assertEquals(200, created.statusCode());
        String playerId = idFrom(created.body());

        var retrieved = request("GET", "/players/" + playerId, null);
        assertEquals(200, retrieved.statusCode());
        assertTrue(retrieved.body().contains("\"name\":\"Alice\""));
        assertTrue(retrieved.body().contains("\"teamId\":\"" + teamId + "\""));
    }

    @Test
    void unknownMatchHasNoEligiblePlayers() throws Exception {
        var response = request("GET", "/players/eligible/" + UUID.randomUUID(), null);

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void rejectsMalformedJson() throws Exception {
        assertEquals(400, request("POST", "/players", "{invalid").statusCode());
    }

    private String idFrom(String json) {
        var matcher = ID.matcher(json);
        assertTrue(matcher.find(), () -> "Missing id in: " + json);
        return matcher.group(1);
    }

    private HttpResponse<String> request(String method, String path, String body) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(body));
        }
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }
}
