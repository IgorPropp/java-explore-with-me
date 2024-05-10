package ru.practicum.admin.event;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.UpdateEventRequest;
import ru.practicum.error.ObjectNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.enums.State;
import ru.practicum.storage.CategoryStorage;
import ru.practicum.storage.EventStorage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEventService {

    private final EventStorage eventStorage;
    private final CategoryStorage categoryStorage;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional(readOnly = true)
    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories,
                                        String rangeStart, String rangeEnd, Integer from, Integer size) {
        List<State> listState = new ArrayList<>();
        List<Event> listEvents;
        if (states != null) {
            for (String state: states) {
                listState.add(State.valueOf(state));
            }
        }
        final Pageable pageable = PageRequest.of(from, size);
        if (users == null && states == null && rangeStart == null && rangeEnd == null && categories == null) {
            listEvents = eventStorage.findAll(pageable)
                    .getContent();
        } else if (states == null && rangeStart == null && rangeEnd == null) {
            listEvents = eventStorage.searchEventsByAdminWithOutStatesAndRange(users, categories, pageable)
                    .getContent();
        } else if (users == null) {
            listEvents = eventStorage.searchEventsNotUsersGetConditions(listState, categories,
                    LocalDateTime.parse(rangeStart, formatter),
                    LocalDateTime.parse(rangeEnd, formatter), pageable).getContent();
        } else if (categories == null) {
            listEvents = eventStorage.searchEventsNotCategoriesGetConditions(users, listState,
                            LocalDateTime.parse(rangeStart, formatter),
                            LocalDateTime.parse(rangeEnd, formatter), pageable).getContent();
        } else {
            listEvents = eventStorage.searchEventsByAdminGetConditions(users, listState, categories,
                            LocalDateTime.parse(rangeStart, formatter),
                            LocalDateTime.parse(rangeEnd, formatter), pageable).getContent();
        }
        return EventMapper.toListEventFullDto(listEvents);
    }

    public EventFullDto updateEvent(Long eventId, UpdateEventRequest updateEventRequest) {
        Event event = eventStorage.findById(eventId).orElseThrow(() -> new ObjectNotFoundException(
                "Объект не найден. ", String.format("Event id=%d was not found.", eventId)));
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
                            String.format("Category id=%d was not found.", updateEventRequest.getCategory())));
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
                case REJECT_EVENT:
                    if (event.getState().equals(State.PUBLISHED)) {
                        throw new DataIntegrityViolationException(
                                String.format("Cannot cancel the event id=%d because it's not in the right state: %s",
                                        event.getId(), event.getState()));
                    }
                    event.setState(State.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    if (!event.getState().equals(State.PENDING)) {
                        throw new DataIntegrityViolationException(
                                String.format("Cannot publish the event id=%d because it's not in the right state: %s",
                                        event.getId(), event.getState()));
                    }
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
            }
        }
        event = eventStorage.save(event);
        return EventMapper.toEventFullDto(event);
    }
}