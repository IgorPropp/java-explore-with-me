package ru.practicum.admin.location_events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.LocationEventsDto;
import ru.practicum.dto.NewLocationEventsDto;
import ru.practicum.error.BadRequestException;
import ru.practicum.error.ObjectNotFoundException;
import ru.practicum.mapper.LocationEventsMapper;
import ru.practicum.model.LocationEvents;
import ru.practicum.storage.LocationEventsStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminLocationEventsService {

    private final LocationEventsStorage locationEventsStorage;

    @Transactional
    public LocationEventsDto createLocationEvents(NewLocationEventsDto newLocationEventsDto) {
        LocationEvents locationEvents = LocationEventsMapper.newToLocationEvents(newLocationEventsDto);
        if (locationEventsStorage.findByLatAndLon(locationEvents.getLat(), locationEvents.getLon()).isPresent())
            throw new BadRequestException("Запрос составлен с ошибкой. ", "Локация с такими координатами уже существует");
        locationEventsStorage.save(locationEvents);
        return LocationEventsMapper.locationEventsToDto(locationEvents);
    }

    @Transactional
    public void deleteLocationEvents(Long id) {
        locationEventsStorage.findById(id).orElseThrow(() -> new ObjectNotFoundException("Объект не найден. ",
                String.format("Локация с id=%d не найдена.", id)));
        locationEventsStorage.deleteById(id);
    }

    @Transactional
    public LocationEventsDto patchLocationEvents(LocationEventsDto locationEventsDto) {
        LocationEvents locationEvents = locationEventsStorage.findById(locationEventsDto.getId()).orElseThrow(
                () -> new ObjectNotFoundException("Объект не найден. ",
                        String.format("Локация с id=%d не найдена.", locationEventsDto.getId())));
        if (locationEventsDto.getName() != null) {
            locationEvents.setName(locationEventsDto.getName());
        }
        if (locationEventsDto.getLat() != 0.0f) {
            locationEvents.setLat(locationEventsDto.getLat());
        }
        if (locationEventsDto.getLon() != 0.0f) {
            locationEvents.setLon(locationEventsDto.getLon());
        }
        if (locationEventsDto.getRad() != 0.0f) {
            locationEvents.setRad(locationEventsDto.getRad());
        }
        locationEventsStorage.save(locationEvents);
        return LocationEventsMapper.locationEventsToDto(locationEvents);
    }

    public List<LocationEventsDto> getAllLocationEvents() {
        return LocationEventsMapper.toListLocationEventsDto(locationEventsStorage.findAll());
    }

    public LocationEventsDto getLocationEventsById(Long id) {
        LocationEvents locationGroup = locationEventsStorage.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Объект не найден. ",
                        String.format("Локация с id=%d не найдена.", id)));
        return LocationEventsMapper.locationEventsToDto(locationGroup);
    }
}