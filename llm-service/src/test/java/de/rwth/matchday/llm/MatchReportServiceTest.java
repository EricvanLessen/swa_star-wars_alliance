package de.rwth.matchday.llm;

import de.rwth.matchday.llm.application.service.MatchReportService;
import de.rwth.matchday.llm.domain.MatchReportInput;
import de.rwth.matchday.llm.port.out.LoadMatchReportDataPort;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchReportServiceTest {
 @Test void generatesReportWithOnlyProvidedFacts() {
  var data = new MatchReportInput("Alemannia","TTC",LocalDate.of(2026,11,12),6,4,
      List.of("Gutes Doppel"));
  var loader=mock(LoadMatchReportDataPort.class);
  UUID id=UUID.randomUUID();
  when(loader.load(id)).thenReturn(data);
  var service=new MatchReportService((system,user)->{
    assertTrue(user.contains("Alemannia"));
    assertTrue(user.contains("6"));
    assertTrue(system.contains("Erfinde keine"));
    return "Bericht";
  },loader);
  assertEquals("Bericht",service.generateFor(id).text());
 }
}

