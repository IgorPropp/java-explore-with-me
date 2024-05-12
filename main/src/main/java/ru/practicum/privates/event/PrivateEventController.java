package ru.practicum.privates.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@Validated
@RequiredArgsConstructor
@Slf4j
public class PrivateEventController {
    private final PrivateEventService privateEventService;

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                             Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET user events userId={}", userId);
        return privateEventService.getUserEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addUserEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        log.info("POST user event userId={}", userId);
        return privateEventService.addUserEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("GET user event userId={}, eventId={}", userId, eventId);
        return privateEventService.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                        @Valid @RequestBody UpdateEventRequest updateEventRequest) {
        log.info("PATCH user event userId={}, eventId={}", userId, eventId);
        return privateEventService.updateUserEvent(userId, eventId, updateEventRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("GET user event requests userId={}, eventId={}", userId, eventId);
        return privateEventService.getUserEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateUserEventRequests(@PathVariable Long userId,
                                                                  @PathVariable Long eventId,
                                                                  @RequestBody EventRequestStatusUpdateRequest requestsUpdate) {
        log.info("PATCH user event requests userId={}, eventId={}", userId, eventId);
        return privateEventService.updateUserEventRequests(userId, eventId, requestsUpdate);
    }
}