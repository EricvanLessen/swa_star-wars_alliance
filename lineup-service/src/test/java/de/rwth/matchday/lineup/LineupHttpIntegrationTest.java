package de.rwth.matchday.lineup;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LineupHttpIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void proposeGetConfirmAndRejectTransitions() throws Exception {
        String matchId = UUID.randomUUID().toString();
        String teamId = UUID.randomUUID().toString();
        String playerId = UUID.randomUUID().toString();
        String json = """
            {"matchId":"%s","teamId":"%s","playerIds":["%s"]}
            """.formatted(matchId, teamId, playerId);

        String response = mvc.perform(post("/lineups").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andReturn().getResponse().getContentAsString();
        String id = mapper.readTree(response).get("id").asText();

        mvc.perform(get("/lineups/" + id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.matchId").value(matchId))
            .andExpect(jsonPath("$.teamId").value(teamId))
            .andExpect(jsonPath("$.playerIds[0]").value(playerId))
            .andExpect(jsonPath("$.status").value("PROPOSED"));

        mvc.perform(post("/lineups/" + id + "/confirm")).andExpect(status().isNoContent());
        mvc.perform(get("/lineups/" + id))
            .andExpect(jsonPath("$.status").value("CONFIRMED"));
        mvc.perform(post("/lineups/" + id + "/reject")).andExpect(status().isConflict());
    }

    @Test
    void rejectsDuplicatePlayersAndMissingLineups() throws Exception {
        String playerId = UUID.randomUUID().toString();
        String json = """
            {"matchId":"%s","teamId":"%s","playerIds":["%s","%s"]}
            """.formatted(UUID.randomUUID(), UUID.randomUUID(), playerId, playerId);
        mvc.perform(post("/lineups").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/lineups/" + UUID.randomUUID())).andExpect(status().isNotFound());
    }
}

