package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.StatsView;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsStorage extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.model.StatsView(s.app, s.uri, COUNT(s.ip))  " +
            "FROM EndpointHit as s " +
            "WHERE s.timestamp > ?2 AND s.timestamp < ?3 AND s.uri IN ?1 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<StatsView> findEndpointHitByUriUniqueFalse(List<String> uri, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.model.StatsView(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM EndpointHit as s " +
            "WHERE s.timestamp > ?2 AND s.timestamp < ?3 AND s.uri IN ?1  " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<StatsView> findEndpointHitByUriUniqueTrue(List<String> uri, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.model.StatsView(s.app, s.uri, COUNT(DISTINCT(s.ip))) " +
            "FROM EndpointHit as s " +
            "WHERE s.timestamp > ?1 AND s.timestamp < ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<StatsView> findEndpointHitUniqueTrue(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.model.StatsView(s.app, s.uri, COUNT(s.ip)) " +
            "FROM EndpointHit as s " +
            "WHERE s.timestamp > ?1 AND s.timestamp < ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<StatsView> findEndpointHitUniqueFalse(LocalDateTime start, LocalDateTime end);
}