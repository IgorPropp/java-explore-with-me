package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface RequestStorage extends JpaRepository<ParticipationRequest, Long> {

    @Query("select r from ParticipationRequest r " +
            " JOIN Event e ON r.event.id = e.id" +
            " where e.id = ?1 and e.initiator.id = ?2 ")
    Optional<List<ParticipationRequest>> findRequestUserByIdAndEventById(Long eventId, Long userId);

    @Query("select r from ParticipationRequest r where r.requester.id = ?1")
    Optional<List<ParticipationRequest>> findAllRequestUserById(Long id);

    @Query("select r from ParticipationRequest r where r.requester.id = ?1 and r.id = ?2 and r.status <> 'CONFIRMED'")
    Optional<ParticipationRequest> findRequestById(Long userId, Long id);

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    List<ParticipationRequest> findAllByIdInAndAndEventId(Iterable<Long> ids, Long eventId);
}