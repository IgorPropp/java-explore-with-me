package ru.practicum.privates.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {

    private final PrivateRequestService privateRequestService;

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        return privateRequestService.getUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addUserRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        return privateRequestService.addUserRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelUserRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return privateRequestService.cancelUserRequest(userId, requestId);
    }
}