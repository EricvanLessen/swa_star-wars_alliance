package de.rwth.matchday.llm.adapter.out.llm;

import com.fasterxml.jackson.databind.JsonNode;
import de.rwth.matchday.llm.port.out.LanguageModelPort;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 * KI:connect uses an OpenAI-compatible chat-completions endpoint.
 * API credentials and the model display name come only from configuration.
 */
@Component
@ConditionalOnProperty(name = "llm.provider", havingValue = "kiconnect")
public class KiConnectChatAdapter implements LanguageModelPort {
    private final RestClient client;
    private final String model;

    public KiConnectChatAdapter(
            @Value("${ki.connect.base-url}") String baseUrl,
            @Value("${ki.connect.api-key}") String apiKey,
            @Value("${ki.connect.model}") String model) {
        if (apiKey == null || apiKey.isBlank())
            throw new IllegalArgumentException("KI_CONNECT_API_KEY is required in kiconnect mode");
        if (model == null || model.isBlank())
            throw new IllegalArgumentException("KI_CONNECT_MODEL must name an API-enabled model");
        this.client = RestClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .build();
        this.model = model;
    }

    @Override
    public String complete(String systemPrompt, String userPrompt) {
        var body = Map.of("model", model, "messages", List.of(
            Map.of("role", "system", "content", systemPrompt),
            Map.of("role", "user", "content", userPrompt)));
        try {
            JsonNode response = client.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);

            String text = response == null ? "" :
                response.path("choices").path(0).path("message").path("content").asText("");
            if (text.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                "KI:connect returned no chat completion");
            return text;
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                "KI:connect request failed", e);
        }
    }
}

