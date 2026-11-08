package de.rwth.matchday.match;

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

@SpringBootTest @AutoConfigureMockMvc
class MatchHttpIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test void completesConfirmedResultWorkflow() throws Exception {
        String teamId=UUID.randomUUID().toString();
        String json=mvc.perform(post("/matches").contentType(MediaType.APPLICATION_JSON).content(
            """
            {"teamId":"%s","teamName":"Alemannia","opponent":"TTC",
             "date":"2026-11-12","season":"2026-27","venue":"HOME"}
            """.formatted(teamId))).andExpect(status().isCreated())
            .andExpect(jsonPath("$.result").value("PENDING"))
            .andReturn().getResponse().getContentAsString();
        String id=mapper.readTree(json).path("id").asText();

        mvc.perform(get("/matches/"+id+"/confirmed-result")).andExpect(status().isConflict());
        mvc.perform(put("/matches/"+id+"/result").contentType(MediaType.APPLICATION_JSON)
            .content("{\"ownGames\":6,\"opponentGames\":4}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.result").value("OWN_WIN"));
        mvc.perform(post("/matches/"+id+"/confirm")).andExpect(status().isOk())
            .andExpect(jsonPath("$.confirmed").value(true));
        mvc.perform(get("/matches/"+id+"/report-data")).andExpect(status().isOk())
            .andExpect(jsonPath("$.ownGames").value(6));
        mvc.perform(get("/matches/"+id+"/confirmed-result")).andExpect(status().isOk())
            .andExpect(jsonPath("$.season").value("2026-27"));
        mvc.perform(get("/matches/"+UUID.randomUUID())).andExpect(status().isNotFound());
    }
}

