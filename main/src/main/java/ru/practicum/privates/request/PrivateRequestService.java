package ru.practicum.privates.request;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.error.ObjectNotFoundException;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.model.enums.State;
import ru.practicum.model.enums.Status;
import ru.practicum.storage.EventStorage;
import ru.practicum.storage.RequestStorage;
import ru.practicum.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PrivateRequestService {

    private final UserStorage userStorage;
    private final EventStorage eventStorage;
    private final RequestStorage requestStorage;

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        List<ParticipationRequest> listRequest = requestStorage.findAllRequestUserById(userId).orElseThrow(() ->
                new ObjectNotFoundException("Объект не найден. ",
                        String.format("ParticipationRequest list with userId=%d was not found.", userId)));
        return ParticipationRequestMapper.toListParticipationRequestDto(listRequest);
    }

    @Transactional
    public ParticipationRequestDto addUserRequest(Long userId, Long eventId) {
        if (requestStorage.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new DataIntegrityViolationException("Participation request with userId = " + userId
                    + " eventId = " + eventId + " already exists.");
        }
        User requester = userStorage.findById(userId).orElseThrow(() -> new ObjectNotFoundException(
                "Объект не найден. ", "User with id = " + userId + " was not found."));
        Event event = eventStorage.findById(eventId).orElseThrow(() -> new ObjectNotFoundException(
                "Объект не найден. ", "Event with id = " + eventId + " doesn't exist."));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new DataIntegrityViolationException("Users are not allowed to register for unpublished events.");
        }
        if (Objects.equals(userId, event.getInitiator().getId())) {
            throw new DataIntegrityViolationException("Event organizers are not allowed to request participation in" +
                    " their own events.");
        }
        if ((event.getParticipantLimit() != 0L) && (event.getConfirmedRequests() >= event.getParticipantLimit())) {
            throw new DataIntegrityViolationException("Participant limit reached.");
        }
        ParticipationRequest requestToSave = new ParticipationRequest(requester, event,
                !event.getRequestModeration() || event.getParticipantLimit() == 0L ?
                        Status.CONFIRMED : Status.PENDING, LocalDateTime.now());
        ParticipationRequest participationRequest = requestStorage.save(requestToSave);
        if (participationRequest.getStatus() == Status.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventStorage.save(event);
        }
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }

    @Transactional
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        ParticipationRequest participationRequest = requestStorage.findRequestById(userId, requestId).orElseThrow(
                () -> new ObjectNotFoundException("Объект не найден. ",
                        String.format("ParticipationRequest with reqId=%d was not found.", requestId)));
        participationRequest.setStatus(Status.CANCELED);
        Long eventId = participationRequest.getEvent().getId();
        Event event = eventStorage.findById(eventId).orElseThrow(() -> new ObjectNotFoundException(
                "Объект не найден. ", "Event with id=" + eventId + " doesn't exist."));
        event.setConfirmedRequests(event.getConfirmedRequests() - 1);
        eventStorage.save(event);
        requestStorage.save(participationRequest);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }
}