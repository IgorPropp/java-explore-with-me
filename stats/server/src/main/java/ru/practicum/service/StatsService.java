package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.StatsView;
import ru.practicum.storage.StatsStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsStorage statsStorage;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public void createEndpointHit(EndpointHit endpointHit) {
        statsStorage.saveAndFlush(endpointHit);
    }

    @Transactional(readOnly = true)
    public List<StatsView> getStatsView(Optional<String> start, Optional<String> end,
                                            Optional<List<String>> uris, Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start.get(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(end.get(), formatter);
        if (uris.isEmpty()) {
            return unique ? statsStorage.findEndpointHitUniqueTrue(startTime, endTime) :
                    statsStorage.findEndpointHitUniqueFalse(startTime, endTime);
        } else {
            return unique ? statsStorage.findEndpointHitByUriUniqueTrue(uris.get(), startTime, endTime) :
                    statsStorage.findEndpointHitByUriUniqueFalse(uris.get(), startTime, endTime);
        }
    }
}