package de.rwth.matchday.availability;
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
class AvailabilityHttpIntegrationTest {
 @Autowired MockMvc mvc;
 @Test void canDeclareUpdateAndRead() throws Exception {
  String match=UUID.randomUUID().toString(), player=UUID.randomUUID().toString();
  String root="/availabilities/matches/"+match;
  mvc.perform(get(root)).andExpect(status().isOk()).andExpect(jsonPath("$.playerIds.length()").value(0));
  mvc.perform(put(root+"/players/"+player).contentType(MediaType.APPLICATION_JSON).content("{\"available\":true}"))
    .andExpect(status().isOk()).andExpect(jsonPath("$.available").value(true));
  mvc.perform(get(root)).andExpect(jsonPath("$.playerIds[0]").value(player));
  mvc.perform(put(root+"/players/"+player).contentType(MediaType.APPLICATION_JSON).content("{\"available\":false}"))
    .andExpect(status().isOk());
  mvc.perform(get(root)).andExpect(jsonPath("$.playerIds.length()").value(0));
 }
}

