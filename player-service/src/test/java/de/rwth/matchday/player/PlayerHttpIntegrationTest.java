package de.rwth.matchday.player;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles({"mockup","mock-availability"})
class PlayerHttpIntegrationTest {
 @Autowired MockMvc mvc;
 @Autowired ObjectMapper mapper;
 @Test void teamAndPlayerCrudWorks() throws Exception {
  String teamJson=mvc.perform(post("/teams").contentType(MediaType.APPLICATION_JSON)
    .content("{\"name\":\"Example\"}"))
    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  String teamId=mapper.readTree(teamJson).path("id").asText();
  String playerJson=mvc.perform(post("/players").contentType(MediaType.APPLICATION_JSON)
    .content("{\"name\":\"Alice\",\"teamId\":\""+teamId+"\",\"active\":true}"))
    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  String playerId=mapper.readTree(playerJson).path("id").asText();
  mvc.perform(get("/players/"+playerId)).andExpect(status().isOk())
    .andExpect(jsonPath("$.teamId").value(teamId));
  mvc.perform(get("/teams/"+teamId)).andExpect(status().isOk())
    .andExpect(jsonPath("$.members[0]").value(playerId));
  mvc.perform(get("/players/eligible/"+UUID.randomUUID())).andExpect(status().isOk())
    .andExpect(content().json("[]"));
  mvc.perform(delete("/players/"+playerId)).andExpect(status().isOk());
  mvc.perform(get("/teams/"+teamId)).andExpect(jsonPath("$.members.length()").value(0));
 }
 @Test void invalidJson() throws Exception {
  mvc.perform(post("/players").contentType(MediaType.APPLICATION_JSON).content("{invalid"))
    .andExpect(status().isBadRequest());
 }
}

