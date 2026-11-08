package de.rwth.matchday.ranking;

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
class RankingHttpIntegrationTest {
    @Autowired MockMvc mvc;

    @Test
    void confirmedResultUpdatesRankingsAndCanBeCorrected() throws Exception {
        String matchId = UUID.randomUUID().toString();
        String teamId = UUID.randomUUID().toString();
        String url = "/rankings/results/" + matchId;

        mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
                .content(payload("2026-27", teamId, "OWN_WIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.matchId").value(matchId))
            .andExpect(jsonPath("$.result").value("OWN_WIN"));

        mvc.perform(get("/rankings").param("season", "2026-27"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.scoringRule").value("DEMO_WIN_2_DRAW_1_LOSS_0"))
            .andExpect(jsonPath("$.standings[0].teamId").value(teamId))
            .andExpect(jsonPath("$.standings[0].played").value(1))
            .andExpect(jsonPath("$.standings[0].points").value(2));

        mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
                .content(payload("2026-27", teamId, "OPPONENT_WIN")))
            .andExpect(status().isOk());
        mvc.perform(get("/rankings").param("season", "2026-27"))
            .andExpect(jsonPath("$.standings[0].played").value(1))
            .andExpect(jsonPath("$.standings[0].losses").value(1))
            .andExpect(jsonPath("$.standings[0].points").value(0));
    }

    @Test
    void rejectsPendingResultAndBadPayload() throws Exception {
        String url = "/rankings/results/" + UUID.randomUUID();
        mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
                .content(payload("2026-27", UUID.randomUUID().toString(), "PENDING")))
            .andExpect(status().isBadRequest());
        mvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
                .content(payload("2026-27", UUID.randomUUID().toString(), "INVALID")))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/rankings").param("season", " "))
            .andExpect(status().isBadRequest());
    }

    private static String payload(String season, String teamId, String result) {
        return """
            {"season":"%s","teamId":"%s","result":"%s"}
            """.formatted(season, teamId, result);
    }
}

