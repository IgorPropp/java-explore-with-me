package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.StatsView;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    @GetMapping("/statsByIp")
    public ResponseEntity<List<StatsView>> statsByIp(@RequestParam String start,
                                                     @RequestParam String end,
                                                     @RequestParam(required = false) List<String> uris,
                                                     @RequestParam(defaultValue = "false") Boolean unique,
                                                     @RequestParam String ip) {


        LocalDateTime startDT;
        LocalDateTime endDT;
        try {
            startDT = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            endDT = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }

        List<StatsView> results = statsService.getRequestsWithViewsByIp(startDT, endDT, uris, unique, ip);
        return ResponseEntity.ok().body(results);
    }
}