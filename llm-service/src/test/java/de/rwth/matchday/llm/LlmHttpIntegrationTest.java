package de.rwth.matchday.llm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties="llm.provider=mock") @AutoConfigureMockMvc
class LlmHttpIntegrationTest {
 @Autowired MockMvc mvc;
 @Test void sampleJsonWorksOffline() throws Exception {
  mvc.perform(post("/llm/match-reports").contentType(MediaType.APPLICATION_JSON)
    .content("""
      {"team":"Alemannia","opponent":"TTC","date":"2026-11-12","ownGames":6,
       "opponentGames":4,"highlights":[]}
      """)).andExpect(status().isOk()).andExpect(jsonPath("$.text").isNotEmpty());
 }
}

