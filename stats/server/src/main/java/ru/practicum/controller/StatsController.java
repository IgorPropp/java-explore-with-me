package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.StatsView;
import ru.practicum.service.StatsService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public void createEndPointHit(@RequestBody EndpointHit endpointHit) {
        statsService.createEndpointHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<StatsView> getStatsView(@RequestParam("start") Optional<String> start,
                                        @RequestParam("end") Optional<String> end,
                                        @RequestParam("uris") Optional<List<String>> uris,
                                        @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        return statsService.getStatsView(start, end, uris, unique);
    }
}