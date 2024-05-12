package ru.practicum.publics.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsWebClient;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.error.BadRequestException;
import ru.practicum.error.ObjectNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.Event;
import ru.practicum.model.enums.State;
import ru.practicum.storage.EventStorage;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicEventService {

    private final StatsWebClient statsClient = new StatsWebClient("http://localhost:9090");
    private final EventStorage eventStorage;

    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable,
                                         String sort, Integer from, Integer size, HttpServletRequest request) {
        List<Event> listEvent = new ArrayList<>();
        final Pageable pageable = PageRequest.of(from, size);
        if ((rangeStart != null && rangeEnd != null) && (rangeStart.isAfter(rangeEnd) || rangeStart.isEqual(rangeEnd))) {
            throw new BadRequestException("Incorrectly made request.", "Start time must not after or equal to end time.");
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (text == null) {
            text = "%";
        }
        if (sort.equals("EVENT_DATE") && !onlyAvailable && rangeEnd == null) {
            listEvent = eventStorage.searchEventByEventDayAvailableFalseEndNull(text, categories, paid,
                    rangeStart, pageable).getContent();
        }
        if (sort.equals("EVENT_DATE") && !onlyAvailable && rangeEnd != null) {
            listEvent = eventStorage.searchEventByEventDayAvailableFalseEndNotNull(text, categories, paid,
                    rangeStart, rangeEnd, pageable).getContent();
        }
        if (sort.equals("EVENT_DATE") && onlyAvailable && rangeEnd == null) {
            listEvent = eventStorage.searchEventByEventDayAvailableTrueEndNull(text, categories, paid,
                    rangeStart, pageable).getContent();
        }
        if (sort.equals("EVENT_DATE") && onlyAvailable && rangeEnd != null) {
            listEvent = eventStorage.searchEventByEventDayAvailableTrueEndNotNull(text, categories, paid,
                    rangeStart, rangeEnd, pageable).getContent();
        }
        if (sort.equals("VIEWS") && !onlyAvailable && rangeEnd == null) {
            listEvent = eventStorage.searchEventByViewsAvailableFalseEndNull(text, categories, paid,
                    rangeStart, pageable).getContent();
        }
        if (sort.equals("VIEWS") && !onlyAvailable && rangeEnd != null) {
            listEvent = eventStorage.searchEventByViewsAvailableFalseEndNotNull(text, categories, paid,
                    rangeStart, rangeEnd, pageable).getContent();
        }
        if (sort.equals("VIEWS") && onlyAvailable && rangeEnd == null) {
            listEvent = eventStorage.searchEventByViewsAvailableTrueEndNull(text, categories, paid,
                    rangeStart, pageable).getContent();
        }
        if (sort.equals("VIEWS") && onlyAvailable && rangeEnd != null) {
            listEvent = eventStorage.searchEventByViewsAvailableTrueEndNotNull(text, categories, paid,
                    rangeStart, rangeEnd, pageable).getContent();
        }
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setUri(request.getRequestURI());
        endpointHit.setIp(request.getRemoteAddr());
        endpointHit.setTimestamp(LocalDateTime.now());
        endpointHit.setApp("main");
        statsClient.addRequest(endpointHit);
        return EventMapper.toListEventShortDto(listEvent);
    }

    @Transactional
    public EventFullDto getEvent(Long id, HttpServletRequest request) {
        Event event = eventStorage.findByIdAndState(id, State.PUBLISHED).orElseThrow(() -> new ObjectNotFoundException(
                "Объект не найден. ", String.format("Events with id=%d was not found.", id)));
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setUri(request.getRequestURI());
        endpointHit.setIp(request.getRemoteAddr());
        endpointHit.setTimestamp(LocalDateTime.now());
        endpointHit.setApp("main");
        ResponseEntity<List<EndpointHit>> listResponseEntity = statsClient.getStatsByIp(
                LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                Collections.singletonList(endpointHit.getUri()), true, request.getRemoteAddr());
        statsClient.addRequest(endpointHit);
        List<Event> events = Collections.singletonList(event);
        if (listResponseEntity.getStatusCode() == HttpStatus.OK &&
                Optional.ofNullable(listResponseEntity.getBody())
                        .map(List::isEmpty).orElse(false)) {
            events.forEach(eventio -> eventio.setViews(eventio.getViews() + 1));
            eventStorage.saveAll(events);
        }
        return EventMapper.toEventFullDto(event);
    }
}