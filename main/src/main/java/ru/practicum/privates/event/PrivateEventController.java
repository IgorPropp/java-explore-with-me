package ru.practicum.privates.event;

import lombok.RequiredArgsConstructor;
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
public class PrivateEventController {
    private final PrivateEventService privateEventService;

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                             Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return privateEventService.getUserEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addUserEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        return privateEventService.addUserEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return privateEventService.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                        @Valid @RequestBody UpdateEventRequest updateEventRequest) {
        return privateEventService.updateUserEvent(userId, eventId, updateEventRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        return privateEventService.getUserEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateUserEventRequests(@PathVariable Long userId,
                                                                  @PathVariable Long eventId,
                                                                  @RequestBody EventRequestStatusUpdateRequest requestsUpdate) {
        return privateEventService.updateUserEventRequests(userId, eventId, requestsUpdate);
    }
}