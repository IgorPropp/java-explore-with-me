package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.LocationEvents;

import java.util.Optional;

public interface LocationEventsStorage extends JpaRepository<LocationEvents, Long> {

    Optional<LocationEvents> findByLatAndLon(float lat, float lon);
}