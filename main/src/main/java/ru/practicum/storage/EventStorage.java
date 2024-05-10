package ru.practicum.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Event;
import ru.practicum.model.enums.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventStorage extends JpaRepository<Event, Long> {

    Optional<Event> findByIdAndState(Long id, State state);

    @Query("select e from Event e " +
            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
            " or upper(e.description) like upper(concat('%', ?1, '%')) " +
            "and e.state = 'PUBLISHED' and e.category.id = ?2 and e.paid = ?3 " +
            "and e.eventDate > e.createdOn ORDER BY e.eventDate asc ")
    Page<Event> searchEventByEventDayAvailableFalseEndNull(String text, List<Long> category, Boolean paid,
                                                           LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event e " +
            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
            " or upper(e.description) like upper(concat('%', ?1, '%')) " +
            "and e.state = 'PUBLISHED' and e.category.id = ?2 and e.paid = ?3 " +
            "and e.eventDate between ?4 and ?5 ORDER BY e.eventDate asc ")
    Page<Event> searchEventByEventDayAvailableFalseEndNotNull(String text, List<Long> category, Boolean paid,
                                                              LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e from Event e " +
            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
            " or upper(e.description) like upper(concat('%', ?1, '%')) " +
            "and e.state = 'PUBLISHED' and e.category.id = ?2 and e.paid = ?3 " +
            "and e.eventDate > e.createdOn ORDER BY e.eventDate asc ")
    Page<Event> searchEventByEventDayAvailableTrueEndNull(String text, List<Long> category, Boolean paid,
                                                          LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event e " +
            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
            " or upper(e.description) like upper(concat('%', ?1, '%')) " +
            "and e.state = 'PUBLISHED' and e.category.id = ?2 and e.paid = ?3 " +
            "and e.eventDate between ?4 and ?5 ORDER BY e.eventDate asc ")
    Page<Event> searchEventByEventDayAvailableTrueEndNotNull(String text, List<Long> category, Boolean paid,
                                                             LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                             Pageable pageable);

    @Query("select e from Event e where e.initiator.id = ?1 and e.state = ?2 " +
            "and e.category.id = ?3 and e.eventDate between ?4 and ?5 ")
    Page<Event> searchEventsByAdminGetConditions(List<Long> usersId, List<State> states, List<Long> categoriesId,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e from Event e where e.state = ?1 " +
            "and e.category.id = ?2 and e.eventDate between ?3 and ?4 ")
    Page<Event> searchEventsNotUsersGetConditions(List<State> states, List<Long> categoriesId,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e from Event e where e.initiator.id = ?1 and e.state = ?2 " +
            "and e.eventDate between ?3 and ?4 ")
    Page<Event> searchEventsNotCategoriesGetConditions(List<Long> usersId, List<State> states,
                                                       LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                       Pageable pageable);

    @Query("select e from Event e where e.initiator.id = ?1 and e.category.id = ?2 ")
    Page<Event> searchEventsByAdminWithOutStatesAndRange(List<Long> usersId, List<Long> categoriesId,
                                                         Pageable pageable);

    @Query("select e from Event e where e.initiator.id = ?1 ")
    Page<Event> findEventsByUserId(List<Long> id, Pageable pageable);

    @Query("select e from Event e " +
            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
            " or upper(e.description) like upper(concat('%', ?1, '%')) " +
            "and e.state = 'PUBLISHED' and e.category.id = ?2 and e.paid = ?3 " +
            "and e.eventDate > e.createdOn ORDER BY e.views ")
    Page<Event> searchEventByViewsAvailableFalseEndNull(String text, List<Long> category, Boolean paid,
                                                        LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event e " +
            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
            " or upper(e.description) like upper(concat('%', ?1, '%')) " +
            "and e.state = 'PUBLISHED' and e.category.id = ?2 and e.paid = ?3 " +
            "and e.eventDate between ?4 and ?5 ORDER BY e.views desc ")
    Page<Event> searchEventByViewsAvailableFalseEndNotNull(String text, List<Long> category, Boolean paid,
                                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                           Pageable pageable);

    @Query("select e from Event e " +
            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
            " or upper(e.description) like upper(concat('%', ?1, '%')) " +
            "and e.state = 'PUBLISHED' and e.category.id = ?2 and e.paid = ?3 " +
            "and e.eventDate > e.createdOn ORDER BY e.views desc ")
    Page<Event> searchEventByViewsAvailableTrueEndNull(String text, List<Long> category, Boolean paid,
                                                       LocalDateTime rangeStart, Pageable pageable);

    @Query("select e from Event e " +
            "where upper(e.annotation) like upper(concat('%', ?1, '%')) " +
            " or upper(e.description) like upper(concat('%', ?1, '%')) " +
            "and e.state = 'PUBLISHED' and e.category.id = ?2 and e.paid = ?3 " +
            "and e.eventDate between ?4 and ?5 ORDER BY e.views desc ")
    Page<Event> searchEventByViewsAvailableTrueEndNotNull(String text, List<Long> category, Boolean paid,
                                                          LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                          Pageable pageable);

    @Query("select e from Event e where e.initiator.id = ?1 and e.id = ?2 ")
    Optional<Event> findUserEventById(Long userId, Long eventId);

    @Query("select e from Event e where e.category.id = ?1 ")
    Optional<List<Event>> findCategoryByIdInEvent(Long catId);

    List<Event> findAllByIdIn(Set<Long> events);

}