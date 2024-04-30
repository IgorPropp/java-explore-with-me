package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.StatsView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsWebClient {

    private final WebClient webClient;

    public EndpointHit createEndPointHit(EndpointHit hitsDto) {
        String uri = "/hit";
        return webClient
                .post()
                .uri(uri)
                .bodyValue(hitsDto)
                .retrieve()
                .bodyToMono(EndpointHit.class)
                .block();
    }

    public List<StatsView> getStatsView(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String requestUrl = String.format("/stats?start=%s&end=%s&uris=%s&unique=%s",
                URLEncoder.encode(String.valueOf(start), StandardCharsets.UTF_8),
                URLEncoder.encode(String.valueOf(end), StandardCharsets.UTF_8),
                uris,
                unique);
        return webClient.get()
                .uri(requestUrl)
                .retrieve()
                .bodyToFlux(StatsView.class)
                .collectList()
                .block();
    }
}