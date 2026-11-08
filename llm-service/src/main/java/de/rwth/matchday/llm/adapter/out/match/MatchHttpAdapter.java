package de.rwth.matchday.llm.adapter.out.match;

import de.rwth.matchday.llm.domain.MatchReportInput;
import de.rwth.matchday.llm.port.out.LoadMatchReportDataPort;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class MatchHttpAdapter implements LoadMatchReportDataPort {
    public record ReportData(UUID matchId,String team,String opponent,LocalDate date,
                             int ownGames,int opponentGames,List<String> highlights) {}
    private final RestClient client;
    public MatchHttpAdapter(@Value("${match.service.url}") String baseUrl) {
        client=RestClient.builder().baseUrl(baseUrl).build();
    }
    @Override public MatchReportInput load(UUID matchId) {
        try {
            ReportData data=client.get().uri("/matches/{id}/report-data",matchId)
                .retrieve().body(ReportData.class);
            if (data==null) throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                "Match Service returned no report data");
            return new MatchReportInput(data.team(),data.opponent(),data.date(),
                data.ownGames(),data.opponentGames(),data.highlights());
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                "Could not fetch confirmed result from Match Service",e);
        }
    }
}

