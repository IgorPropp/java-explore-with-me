package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Location;

import java.util.Optional;

public interface LocationStorage extends JpaRepository<Location, Long> {

    Optional<Location> findByLatAndLon(float lat, float lon);

}