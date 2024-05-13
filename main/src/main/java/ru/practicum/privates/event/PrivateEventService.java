package ru.practicum.privates.event;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.*;
import ru.practicum.error.BadRequestException;
import ru.practicum.error.ConditionsAreNotMetException;
import ru.practicum.error.ObjectNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.User;
import ru.practicum.model.enums.State;
import ru.practicum.model.enums.Status;
import ru.practicum.storage.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PrivateEventService {

    private final UserStorage userStorage;
    private final CategoryStorage categoryStorage;
    private final EventStorage eventStorage;
    private final RequestStorage requestStorage;
    private final LocationStorage locationStorage;

    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        final Pageable pageable = PageRequest.of(from, size);
        List<Event> listEvent = eventStorage.findEventsByUserId(List.of(userId), pageable).getContent();
        return EventMapper.toListEventShortDto(listEvent);
    }

    @Transactional
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventRequest updateEventRequest) {
        Event event = eventStorage.findUserEventById(userId, eventId).orElseThrow(() -> new ObjectNotFoundException(
                "Объект не найден. ", String.format("Event with id=%d userId=%d was not found.",
                eventId, userId)));
        Event updatedEvent = eventValidation(event, updateEventRequest);
        eventStorage.saveAndFlush(updatedEvent);
        return EventMapper.toEventFullDto(updatedEvent);
    }

    @Transactional
    public EventFullDto addUserEvent(Long userId, NewEventDto newEventDto) {
        User user = userValidation(userId);
        if (locationStorage.findByLatAndLon(newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon()).isPresent()) {
            throw new BadRequestException("Запрос составлен с ошибкой. ", "locationRepository");
        }
        if (!newEventDto.getEventDate().minusHours(2).isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Запрос составлен с ошибкой. ", "EventDate");
        }
        Category category = categoryStorage.findById(newEventDto.getCategory()).orElseThrow(() ->
                new ObjectNotFoundException("Объект не найден. ", String.format("Category with id=%d was not found.",
                        newEventDto.getCategory())));
        Event event = EventMapper.toEvent(newEventDto);
        event.setCategory(category);
        event.setInitiator(user);
        locationStorage.save(newEventDto.getLocation());
        eventStorage.save(event);
        return EventMapper.toEventFullDto(event);
    }

    @Transactional(readOnly = true)
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        userValidation(userId);
        Event event = eventStorage.findUserEventById(userId, eventId).orElseThrow(() ->
                new ObjectNotFoundException("Объект не найден. ",
                        String.format("Event with eventId=%d and userId=%d was not found.", eventId, userId)));
        return EventMapper.toEventFullDto(event);
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId) {
        List<ParticipationRequest> participationRequest = requestStorage.findRequestUserByIdAndEventById(eventId,
                userId).orElseThrow(() -> new ObjectNotFoundException("Объект не найден. ",
                String.format("ParticipationRequest with userId=%d, eventId=%d was not found.", userId, eventId)));
        return ParticipationRequestMapper.toListParticipationRequestDto(participationRequest);
    }

    public EventRequestStatusUpdateResult updateUserEventRequests(Long userId, Long eventId,
                                                                  EventRequestStatusUpdateRequest updateRequest) {
        Event event = eventStorage.findUserEventById(userId, eventId).orElseThrow(() -> new ObjectNotFoundException(
                "Объект не найден. ", "Event with id = " + eventId + " and user id = " + userId + " doesn't exist."));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConditionsAreNotMetException("Не выполнены условия для совершения операции",
                    "Access denied. User with id = " + userId + " is not an event initiator.");
        }
        List<ParticipationRequest> participationRequests = requestStorage.findAllByIdInAndAndEventId(
                updateRequest.getRequestIds(), eventId);
        if (participationRequests.size() != updateRequest.getRequestIds().size()) {
            throw new ObjectNotFoundException("Объект не найден. ",
                    "Incorrect request id(s) received in the request body.");
        }
        for (ParticipationRequest request : participationRequests) {
            if (!request.getStatus().equals(Status.PENDING)) {
                throw new DataIntegrityViolationException(
                        "Only requests with status 'Pending' can be accepted or rejected.");
            }
        }
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        if (updateRequest.getStatus() == Status.REJECTED) {
            participationRequests.forEach(participationRequest -> {
                participationRequest.setStatus(Status.REJECTED);
                requestStorage.save(participationRequest);
                rejectedRequests.add(ParticipationRequestMapper.toParticipationRequestDto(participationRequest));
            });
            return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        }
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            return new EventRequestStatusUpdateResult(
                    participationRequests.stream()
                            .map(ParticipationRequestMapper::toParticipationRequestDto)
                            .collect(Collectors.toList()),
                    new ArrayList<>()
            );
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new DataIntegrityViolationException(
                    "Failed to accept request. Reached max participant limit for event id = " + eventId + ".");
        }
        participationRequests.forEach(participationRequest -> {
            if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                participationRequest.setStatus(Status.CONFIRMED);
                requestStorage.save(participationRequest);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                confirmedRequests.add(ParticipationRequestMapper.toParticipationRequestDto(participationRequest));
            } else {
                participationRequest.setStatus(Status.REJECTED);
                requestStorage.save(participationRequest);
                rejectedRequests.add(ParticipationRequestMapper.toParticipationRequestDto(participationRequest));
            }
        });
        if (!confirmedRequests.isEmpty()) {
            eventStorage.save(event);
        }
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private User userValidation(Long userId) throws ObjectNotFoundException {
        return userStorage.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Объект не найден. ",
                String.format("User with id=%d was not found.", userId)));
    }

    private Event eventValidation(Event event, UpdateEventRequest updateEventRequest) {
        if (event.getState().equals(State.PUBLISHED)) {
            throw new DataIntegrityViolationException("Event is already published");
        }
        if (updateEventRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getTitle() != null) {
            event.setTitle(updateEventRequest.getTitle());
        }
        if (updateEventRequest.getDescription() != null) {
            event.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getCategory() != null) {
            Category category = categoryStorage.findById(updateEventRequest.getCategory())
                    .orElseThrow(() -> new ObjectNotFoundException("Объект не найден. ",
                            String.format("Category with id=%d was not found.", updateEventRequest.getCategory())));
            event.setCategory(category);
        }
        if (updateEventRequest.getLocation() != null) {
            event.setLocation(updateEventRequest.getLocation());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getEventDate() != null) {
            event.setEventDate(updateEventRequest.getEventDate());
        }
        if (updateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventRequest.getRequestModeration());
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getStateAction() != null) {
            switch (updateEventRequest.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    break;
            }
        }
        return event;
    }
}