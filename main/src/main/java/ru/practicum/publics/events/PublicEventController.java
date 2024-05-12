package ru.practicum.publics.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@Validated
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {

    private final PublicEventService publicEventService;

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(name = "text", required = false) String text,
                                         @RequestParam(name = "categories", required = false) List<Long> categories,
                                         @RequestParam(name = "paid", required = false) Boolean paid,
                                         @RequestParam(name = "rangeStart", required = false)
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                         @RequestParam(name = "rangeEnd", required = false)
                                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                         @RequestParam(name = "onlyAvailable", required = false) boolean onlyAvailable,
                                         @RequestParam(name = "sort", defaultValue = "VIEWS") String sort,
                                         @PositiveOrZero  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        log.info("GET public events text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
        return publicEventService.getEvents(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        log.info("GET public event eventId={}", id);
        return publicEventService.getEvent(id, request);
    }
}