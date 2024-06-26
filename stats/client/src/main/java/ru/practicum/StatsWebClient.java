package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.model.EndpointHit;

import java.util.List;

@Component
public class StatsWebClient {
    private final WebClient webClient;

    public StatsWebClient(@Value("http://localhost:9090") String statsServerUrl) {
        webClient = WebClient.create(statsServerUrl);
    }

    public void addRequest(EndpointHit requestDto) {
        webClient.post().uri("/hit").bodyValue(requestDto).retrieve().bodyToMono(Object.class).block();
    }

    public ResponseEntity<List<EndpointHit>> getStats(String start, String end, List<String> uris, Boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/stats")
                            .queryParam("start", start)
                            .queryParam("end", end);
                    if (uris != null)
                        uriBuilder.queryParam("uris", String.join(",", uris));
                    if (unique != null)
                        uriBuilder.queryParam("unique", unique);
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResponseEntity<List<EndpointHit>>>() {})
                .block();
    }

    public ResponseEntity<List<EndpointHit>> getStatsByIp(String start, String end, List<String> uris,
                                                          Boolean unique, String ip) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/statsByIp")
                            .queryParam("start", start)
                            .queryParam("end", end)
                            .queryParam("ip", ip);
                    if (uris != null)
                        uriBuilder.queryParam("uris", String.join(",", uris));
                    if (unique != null)
                        uriBuilder.queryParam("unique", unique);
                    return uriBuilder.build();
                })
                .retrieve()
                .toEntityList(EndpointHit.class)
                .block();
    }
}