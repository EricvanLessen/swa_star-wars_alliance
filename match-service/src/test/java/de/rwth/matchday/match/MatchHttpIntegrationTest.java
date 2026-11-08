package de.rwth.matchday.match;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MatchHttpIntegrationTest {
    private static final Pattern ID = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");
    private static final AtomicInteger upstreamStatus = new AtomicInteger(200);
    private static final AtomicReference<String> upstreamRequest = new AtomicReference<>();
    private static final HttpServer upstream = startUpstream();
    private final HttpClient client = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void playerServiceUrl(DynamicPropertyRegistry registry) {
        registry.add("player.service.url", () -> "http://localhost:" + upstream.getAddress().getPort());
    }

    @AfterAll
    static void stopUpstream() {
        upstream.stop(0);
    }

    @Test
    void createsMatchAndRequestsLineupFromPlayerServiceOverHttp() throws Exception {
        upstreamStatus.set(200);
        var created = request("POST", "/matches",
                "{\"opponent\":\"Opponents\",\"date\":\"2026-11-12\",\"teamSize\":2}");
        assertEquals(201, created.statusCode());
        var matcher = ID.matcher(created.body());
        assertTrue(matcher.find(), () -> "Missing id in: " + created.body());
        String id = matcher.group(1);

        var retrieved = request("GET", "/matches/" + id, null);
        assertEquals(200, retrieved.statusCode());
        assertTrue(retrieved.body().contains("\"opponent\":\"Opponents\""));

        var lineup = request("POST", "/matches/" + id + "/lineup/propose", "");
        assertEquals(200, lineup.statusCode());
        assertTrue(lineup.body().contains("\"complete\":true"));
        assertTrue(lineup.body().contains("\"id\":1"));
        assertTrue(lineup.body().contains("\"id\":3"));
        assertTrue(lineup.body().indexOf("\"id\":1") < lineup.body().indexOf("\"id\":3"));
        assertEquals("/players/eligible?matchId=" + id, upstreamRequest.get());
    }

    @Test
    void returnsBadGatewayWhenPlayerServiceFails() throws Exception {
        var created = request("POST", "/matches",
                "{\"opponent\":\"Opponents\",\"date\":\"2026-11-12\",\"teamSize\":2}");
        assertEquals(201, created.statusCode());
        var matcher = ID.matcher(created.body());
        assertTrue(matcher.find());
        upstreamStatus.set(503);
        try {
            assertEquals(502, request("POST", "/matches/" + matcher.group(1) + "/lineup/propose", "").statusCode());
        } finally {
            upstreamStatus.set(200);
        }
    }

    @Test
    void rejectsInvalidAndUnknownMatches() throws Exception {
        assertEquals(400, request("POST", "/matches",
                "{\"opponent\":\"\",\"date\":\"2026-11-12\",\"teamSize\":0}").statusCode());
        assertEquals(404, request("GET", "/matches/999999", null).statusCode());
    }

    private static HttpServer startUpstream() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/", exchange -> {
                upstreamRequest.set(exchange.getRequestURI().toString());
                byte[] body = "[{\"id\":3,\"name\":\"Carol\",\"active\":true},{\"id\":1,\"name\":\"Alice\",\"active\":true}]"
                        .getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(upstreamStatus.get(), body.length);
                try (var output = exchange.getResponseBody()) {
                    output.write(body);
                }
            });
            server.start();
            return server;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
