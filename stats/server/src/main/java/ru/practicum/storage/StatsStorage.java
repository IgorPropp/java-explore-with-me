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

    @Query("SELECT new ru.practicum.model.StatsView(r.app, r.uri, COUNT(r.ip)) " +
            "FROM EndpointHit as r " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "AND r.uri IN (?3) " +
            "GROUP BY r.app, r.uri " +
            "ORDER BY COUNT(r.ip) DESC ")
    List<StatsView> getAllRequestsWithUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.model.StatsView(r.app, r.uri, COUNT(DISTINCT r.ip)) " +
            "FROM EndpointHit as r " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "AND r.uri IN (?3) " +
            "GROUP BY r.app, r.uri " +
            "ORDER BY COUNT(DISTINCT r.ip) DESC ")
    List<StatsView> getUniqueIpRequestsWithUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT new ru.practicum.model.StatsView(r.app, r.uri, COUNT(DISTINCT r.ip)) " +
            "FROM EndpointHit as r " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "GROUP BY r.app, r.uri " +
            "ORDER BY COUNT(DISTINCT r.ip) DESC ")
    List<StatsView> getUniqueIpRequestsWithoutUri(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT new ru.practicum.model.StatsView(r.app, r.uri, COUNT(r.ip)) " +
            "FROM EndpointHit as r " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "GROUP BY r.app, r.uri " +
            "ORDER BY COUNT(r.ip) DESC ")
    List<StatsView> getAllRequestsWithoutUri(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT new ru.practicum.model.StatsView(r.app, r.uri, COUNT(r.ip)) " +
            "FROM EndpointHit as r " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "AND r.uri IN (?3) " +
            "AND r.ip = ?4 " +
            "GROUP BY r.app, r.uri " +
            "ORDER BY COUNT(r.ip) DESC ")
    List<StatsView> getAllRequestsWithUriByIp(LocalDateTime start, LocalDateTime end, List<String> uris, String ip);

    @Query(value = "SELECT new ru.practicum.model.StatsView(r.app, r.uri, COUNT(DISTINCT r.ip)) " +
            "FROM EndpointHit as r " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "AND r.uri IN (?3) " +
            "AND r.ip = ?4 " +
            "GROUP BY r.app, r.uri " +
            "ORDER BY COUNT(DISTINCT r.ip) DESC ")
    List<StatsView> getUniqueIpRequestsWithUriByIp(LocalDateTime start, LocalDateTime end, List<String> uris, String ip);

    @Query(value = "SELECT new ru.practicum.model.StatsView(r.app, r.uri, COUNT(DISTINCT r.ip)) " +
            "FROM EndpointHit as r " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "AND r.ip = ?3 " +
            "GROUP BY r.app, r.uri " +
            "ORDER BY COUNT(DISTINCT r.ip) DESC ")
    List<StatsView> getUniqueIpRequestsWithoutUriByIp(LocalDateTime start, LocalDateTime end, String ip);

    @Query(value = "SELECT new ru.practicum.model.StatsView(r.app, r.uri, COUNT(r.ip)) " +
            "FROM EndpointHit as r " +
            "WHERE r.timestamp between ?1 AND ?2 " +
            "AND r.ip = ?3 " +
            "GROUP BY r.app, r.uri " +
            "ORDER BY COUNT(r.ip) DESC ")
    List<StatsView> getAllRequestsWithoutUriByIp(LocalDateTime start, LocalDateTime end, String ip);
}